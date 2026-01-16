// Hàm tiện ích để bật/tắt loading cho một khu vực cụ thể
function setLoading(isLoading, selectId, spinnerId) {
    const select = document.getElementById(selectId);
    const spinner = document.getElementById(spinnerId);

    if (isLoading) {
        spinner.classList.remove('d-none'); // Hiện spinner
        select.disabled = true;             // Khóa ô chọn
    } else {
        spinner.classList.add('d-none');    // Ẩn spinner
        select.disabled = false;            // Mở ô chọn
    }
}

async function fetchJSON(url, options) {
    const res = await fetch(url, options || {});
    if (!res.ok) throw new Error('HTTP ' + res.status);
    return await res.json();
}

// --- CẬP NHẬT HÀM LOAD QUỐC GIA ---
async function loadCountriesAsia() {
    const countrySelect = document.getElementById('countrySelect');

    // Bật loading
    setLoading(true, 'countrySelect', 'countrySpinner');
    countrySelect.innerHTML = '<option value="">-- Đang tải dữ liệu... --</option>';

    try {
        const countries = await fetchJSON('/api/countries?continent_id=AS');
        countries.sort((a, b) => (a.name || '').localeCompare(b.name || ''));

        // Xóa text "Đang tải" và thêm option mặc định
        countrySelect.innerHTML = '<option value="">-- Select a country --</option>';

        for (const c of countries) {
            const opt = document.createElement('option');
            opt.value = c.code;
            opt.textContent = c.name + (c.code ? ' (' + c.code + ')' : '');
            countrySelect.appendChild(opt);
        }
    } catch (e) {
        console.error('Failed to load countries', e);
        countrySelect.innerHTML = '<option value="">Error loading data</option>';
    } finally {
        // Tắt loading dù thành công hay thất bại
        setLoading(false, 'countrySelect', 'countrySpinner');
    }
}

// --- CẬP NHẬT HÀM LOAD SÂN BAY ---
async function loadAirportsForCountry(countryCode) {
    const airportSelect = document.getElementById('airportSelect');
    const helper = document.getElementById('airportHelper');

    // Reset và Bật loading
    helper.textContent = '';
    airportSelect.innerHTML = '<option value="">-- Loading airports... --</option>';
    setLoading(true, 'airportSelect', 'airportSpinner');

    try {
        let airports = await fetchJSON('/api/airports?country_code=' + encodeURIComponent(countryCode));

        airportSelect.innerHTML = '<option value="">-- Select an airport --</option>';
        airports.sort((a, b) => (a.name || '').localeCompare(b.name || ''));

        for (const a of airports) {
            const opt = document.createElement('option');
            opt.value = a.iataCode;
            const code = a.iataCode ? a.iataCode : (a.icaoCode || '');
            opt.textContent = (a.name || code) + (code ? ' (' + code + ')' : '');
            airportSelect.appendChild(opt);
        }

        if (airports.length === 0) {
            helper.textContent = 'No airports found for this country.';
        } else {
            helper.textContent = ''; // Xóa thông báo cũ nếu thành công
        }

    } catch (e) {
        airportSelect.innerHTML = '<option value="">-- Error loading airports --</option>';
        helper.textContent = 'An error occurred while loading the airport list.';
        console.error('Failed to load airports', e);
    } finally {
        // Tắt loading
        setLoading(false, 'airportSelect', 'airportSpinner');
    }
}

// Các hàm khác giữ nguyên (onCountryChange, renderFlights, onSearch, event listener...)
function onCountryChange(ev) {
    const countryCode = ev.target.value;
    const airportSelect = document.getElementById('airportSelect');
    airportSelect.innerHTML = '<option value="">-- Please select a country first --</option>';
    if (countryCode) {
        loadAirportsForCountry(countryCode);
    }
}

function renderFlights(flights) {
  const container = document.getElementById('flightsContainer');
  container.innerHTML = '';

  const card = document.createElement('div');
  card.className = 'card p-4';

  if (!flights || flights.length === 0) {
    const warn = document.createElement('div');
    warn.className = 'alert alert-warning';
    warn.textContent = 'No flights found or API rate limit exceeded.';
    card.appendChild(warn);
    container.appendChild(card);
    return;
  }

  const table = document.createElement('table');
  table.className = 'table table-striped table-hover';
  table.innerHTML = `
    <thead>
      <tr>
        <th>Airline (IATA)</th>
        <th>Flight (IATA)</th>
        <th>Departure (IATA)</th>
        <th>Departure Time</th>
        <th>Departure Time UTC</th>
        <th>Arrival (IATA)</th>
        <th>Arrival Time</th>
        <th>Arrival Time UTC</th>
        <th>Status</th>
      </tr>
    </thead>
    <tbody></tbody>`;
  const tbody = table.querySelector('tbody');

  const fmt = (dt) => {
    if (!dt) return '-';
    try {
      const d = new Date(dt);
      return d.toLocaleString('vi-VN', { hour: '2-digit', minute: '2-digit', day: '2-digit', month: '2-digit', year: 'numeric' });
    } catch (_) { return '-'; }
  };

  for (const f of flights) {
    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td>${f.airlineIata ?? '-'}</td>
      <td>${f.flightIata ?? '-'}</td>
      <td>${f.depIata ?? '-'}</td>
      <td>${fmt(f.depTime)}</td>
      <td>${fmt(f.depTimeUtc)}</td>
      <td>${f.arrIata ?? '-'}</td>
      <td>${fmt(f.arrTime)}</td>
      <td>${fmt(f.arrTimeUtc)}</td>
      <td><span class="badge ${f.status === 'active' || f.status === 'landed' ? 'bg-success' : 'bg-secondary'}">${f.status ?? '-'}</span></td>
    `;
    tbody.appendChild(tr);
  }

  card.appendChild(table);
  container.appendChild(card);
}

// Track current flights for filtering
let currentFlights = [];

// Compute status stats
function computeStatusStats(flights) {
  const stats = { landed: 0, active: 0, scheduled: 0 };
  for (const f of flights || []) {
    const s = (f.status || '').toLowerCase();
    if (s in stats) stats[s]++;
  }
  return stats;
}

// Read selected statuses from checkboxes
function getSelectedStatuses() {
  const set = new Set();
  if (document.getElementById('chkActive')?.checked) set.add('active');
  if (document.getElementById('chkLanded')?.checked) set.add('landed');
  if (document.getElementById('chkScheduled')?.checked) set.add('scheduled');
  return set;
}

// Filter flights by selected statuses
function filterFlightsByStatus(flights, selected) {
  if (!selected || selected.size === 0) return flights || [];
  return (flights || []).filter(f => selected.has((f.status || '').toLowerCase()));
}

// Update counters next to each status
function updateStatusBadges(stats) {
  const setText = (id, val) => { const el = document.getElementById(id); if (el) el.textContent = String(val); };
  setText('count-active', stats.active ?? 0);
  setText('count-landed', stats.landed ?? 0);
  setText('count-scheduled', stats.scheduled ?? 0);
}

// Update visible flights count
function updateVisibleCount(n) {
  const el = document.getElementById('visible-count');
  if (el) el.textContent = String(n ?? 0);
}

// Handle changing filters
function onStatusFilterChange() {
  const selected = getSelectedStatuses();
  const filtered = filterFlightsByStatus(currentFlights, selected);
  updateVisibleCount(filtered.length);
  renderFlights(filtered);
}

// Modify onSearch to store flights, compute stats, apply filters, and render
async function onSearch(e) {
  e.preventDefault();
  const airportCode = document.getElementById('airportSelect').value;
  if (!airportCode) return;
  try {
    const flights = await fetchJSON('/api/flights?airport_code=' + encodeURIComponent(airportCode));
    
    // Update danh sach chuyen bay hien tai
    currentFlights = flights || [];
    updateStatusBadges(computeStatusStats(currentFlights));
    const filtered = filterFlightsByStatus(currentFlights, getSelectedStatuses());
    updateVisibleCount(filtered.length);
    renderFlights(filtered);
  } catch (err) {
    console.error(err);
    currentFlights = [];
    updateStatusBadges(computeStatusStats(currentFlights));
    updateVisibleCount(0);
    renderFlights([]);
  }
}

// Wire up filter checkboxes
document.addEventListener('DOMContentLoaded', () => {
  loadCountriesAsia();
  document.getElementById('countrySelect').addEventListener('change', onCountryChange);
  const form = document.getElementById('searchForm');
  if (form) form.addEventListener('submit', onSearch);

  document.getElementById('chkActive')?.addEventListener('change', onStatusFilterChange);
  document.getElementById('chkLanded')?.addEventListener('change', onStatusFilterChange);
  document.getElementById('chkScheduled')?.addEventListener('change', onStatusFilterChange);
});