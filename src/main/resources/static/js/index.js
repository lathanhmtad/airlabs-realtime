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
        countrySelect.innerHTML = '<option value="">-- Chọn quốc gia --</option>';

        for (const c of countries) {
            const opt = document.createElement('option');
            opt.value = c.code;
            opt.textContent = c.name + (c.code ? ' (' + c.code + ')' : '');
            countrySelect.appendChild(opt);
        }
    } catch (e) {
        console.error('Failed to load countries', e);
        countrySelect.innerHTML = '<option value="">Lỗi tải dữ liệu</option>';
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
    airportSelect.innerHTML = '<option value="">-- Đang tải sân bay... --</option>';
    setLoading(true, 'airportSelect', 'airportSpinner');

    try {
        let airports = await fetchJSON('/api/airports?country_code=' + encodeURIComponent(countryCode));

        airportSelect.innerHTML = '<option value="">-- Chọn sân bay --</option>';
        airports.sort((a, b) => (a.name || '').localeCompare(b.name || ''));

        for (const a of airports) {
            const opt = document.createElement('option');
            opt.value = a.iataCode;
            const code = a.iataCode ? a.iataCode : (a.icaoCode || '');
            opt.textContent = (a.name || code) + (code ? ' (' + code + ')' : '');
            airportSelect.appendChild(opt);
        }

        if (airports.length === 0) {
            helper.textContent = 'Không tìm thấy sân bay cho quốc gia này.';
        } else {
            helper.textContent = ''; // Xóa thông báo cũ nếu thành công
        }

    } catch (e) {
        airportSelect.innerHTML = '<option value="">-- Lỗi tải sân bay --</option>';
        helper.textContent = 'Có lỗi khi tải danh sách sân bay.';
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
    airportSelect.innerHTML = '<option value="">-- Vui lòng chọn quốc gia trước --</option>';
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
    warn.textContent = 'Không tìm thấy chuyến bay nào hoặc API hết hạn mức.';
    card.appendChild(warn);
    container.appendChild(card);
    return;
  }

  const table = document.createElement('table');
  table.className = 'table table-striped table-hover';
  table.innerHTML = `
    <thead>
      <tr>
        <th>Hãng (IATA)</th>
        <th>Chuyến bay (IATA)</th>
        <th>Đi (IATA)</th>
        <th>Giờ đi</th>
        <th>Giờ đi UTC</th>
        <th>Đến (IATA)</th>
        <th>Giờ đến</th>
        <th>Giờ đến UTC</th>
        <th>Trạng thái</th>
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

async function onSearch(e) {
  e.preventDefault();
  const airportCode = document.getElementById('airportSelect').value;
  if (!airportCode) return;
  try {
    const flights = await fetchJSON('/api/flights?airport_code=' + encodeURIComponent(airportCode));
    renderFlights(flights);
  } catch (err) {
    console.error(err);
    renderFlights([]);
  }
}

document.addEventListener('DOMContentLoaded', () => {
  loadCountriesAsia();
  document.getElementById('countrySelect').addEventListener('change', onCountryChange);
  const form = document.getElementById('searchForm');
  if (form) form.addEventListener('submit', onSearch);
});