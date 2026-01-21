/**
 * Visits View
 */
function renderVisitsView() {
  console.log('Rendering Visits View');

  var view = document.getElementById('view-visits');
  if (!view) {
    view = document.createElement('section');
    view.id = 'view-visits';
    document.getElementById('view-container').appendChild(view);
  }
  view.innerHTML = '';

  var title = document.createElement('h2');
  title.textContent = 'Visits (Schedule)';
  view.appendChild(title);

  // --- Selectors ---
  var fs = document.createElement('fieldset');
  var legend = document.createElement('legend');
  legend.textContent = 'Select patient and doctor';
  fs.appendChild(legend);

  var labelPatient = document.createElement('label');
  labelPatient.textContent = 'Patient:';
  fs.appendChild(labelPatient);
  var patientSelect = document.createElement('select');
  patientSelect.id = 'visit-patient-select';
  fs.appendChild(patientSelect);
  fs.appendChild(document.createElement('br'));

  var labelDoctor = document.createElement('label');
  labelDoctor.textContent = 'Doctor:';
  fs.appendChild(labelDoctor);
  var doctorSelect = document.createElement('select');
  doctorSelect.id = 'visit-doctor-select';
  fs.appendChild(doctorSelect);
  fs.appendChild(document.createElement('br'));

  // Date range inputs for availability query
  var fromLabel = document.createElement('label'); fromLabel.textContent = 'From date:'; fs.appendChild(fromLabel);
  var fromInput = document.createElement('input'); fromInput.type = 'date';
  var today = new Date();
  fromInput.value = today.toISOString().split('T')[0];
  fs.appendChild(fromInput); fs.appendChild(document.createElement('br'));

  var toLabel = document.createElement('label'); toLabel.textContent = 'To date:'; fs.appendChild(toLabel);
  var toInput = document.createElement('input'); toInput.type = 'date';
  var inAWeek = new Date(); inAWeek.setDate(inAWeek.getDate() + 7);
  toInput.value = inAWeek.toISOString().split('T')[0];
  fs.appendChild(toInput); fs.appendChild(document.createElement('br'));

  var btnFind = document.createElement('button');
  btnFind.textContent = 'Find available slots';
  btnFind.addEventListener('click', function () {
    var patientId = patientSelect.value;
    var doctorId = doctorSelect.value;
    var fromDate = fromInput.value;
    var toDate = toInput.value;
    if (!patientId) { alert('Select patient'); return; }
    if (!doctorId) { alert('Select doctor'); return; }
    if (!fromDate || !toDate) { alert('Select from/to dates'); return; }

    // build from/to as ISO datetimes (start of day / end of day)
    var fromIso = fromDate + 'T00:00';
    var toIso = toDate + 'T23:59';

    var qs = '?patientId=' + encodeURIComponent(patientId) + '&doctorId=' + encodeURIComponent(doctorId)
      + '&from=' + encodeURIComponent(fromIso) + '&to=' + encodeURIComponent(toIso);

    apiGet('/visits/available-hours' + qs)
      .then(function (resp) {
        if (!resp.ok) {
          return resp.text().then(function (txt) {
            setOutput('Status: ' + resp.status + '\n\n' + txt);
            throw new Error('No availability');
          });
        }
        return resp.json();
      })
      .then(function (mapping) {
        // mapping expected: { "2026-01-21T09:00": 3, "2026-01-21T09:15": 3, ... }
        if (!mapping || Object.keys(mapping).length === 0) {
          setOutput('No available slots returned by server.');
          document.getElementById('visit-slots-container').innerHTML = 'No available slots.';
          return;
        }
        // transform mapping into date->[{time, dutyId}, ...]
        var grouped = {};
        Object.keys(mapping).forEach(function (k) {
          // k is ISO like 2026-01-21T09:00:00 or without seconds
          var dt = k;
          // normalize and split
          var parts = dt.split('T');
          var date = parts[0];
          var time = parts[1] ? parts[1].slice(0, 5) : '';
          if (!grouped[date]) grouped[date] = [];
          grouped[date].push({ time: time, dutyId: mapping[k] });
        });
        renderSlotsTable(grouped, patientId, doctorId);
      })
      .catch(function (err) {
        if (err.message !== 'No availability') {
          handleFetchError(err);
        }
      });
  });
  fs.appendChild(btnFind);
  view.appendChild(fs);

  var slotsContainer = document.createElement('div');
  slotsContainer.id = 'visit-slots-container';
  view.appendChild(slotsContainer);

  // --- Load lists ---
  function loadPatients() {
    apiGet('/patients')
      .then(function (r) { return r.json(); })
      .then(function (list) {
        patientSelect.innerHTML = '';
        var d = document.createElement('option'); d.value = ''; d.textContent = '-- Select patient --'; patientSelect.appendChild(d);
        list.forEach(function (p) {
          var opt = document.createElement('option');
          opt.value = p.id;
          opt.textContent = p.firstName + ' ' + p.lastName + ' (' + p.pesel + ')';
          patientSelect.appendChild(opt);
        });
      })
      .catch(function (err) {
        console.error('Failed to load patients', err);
        handleFetchError(err);
      });
  }

  function loadDoctors() {
    apiGet('/doctors')
      .then(function (r) { return r.json(); })
      .then(function (list) {
        doctorSelect.innerHTML = '';
        var d = document.createElement('option'); d.value = ''; d.textContent = '-- Select doctor --'; doctorSelect.appendChild(d);
        list.forEach(function (p) {
          var opt = document.createElement('option');
          opt.value = p.id;
          opt.textContent = p.firstName + ' ' + p.lastName + ' (' + p.specialization + ')';
          doctorSelect.appendChild(opt);
        });
      })
      .catch(function (err) {
        console.error('Failed to load doctors', err);
        handleFetchError(err);
      });
  }

  function renderSlotsTable(mapping, patientId, doctorId) {
    var container = document.getElementById('visit-slots-container');
    container.innerHTML = '';
    var dates = Object.keys(mapping).sort();
    if (dates.length === 0) { container.textContent = 'No available slots.'; return; }

    var table = document.createElement('table');
    table.style.width = '100%';
    table.style.borderCollapse = 'collapse';

    var thead = document.createElement('thead');
    var trh = document.createElement('tr');
    dates.forEach(function (date) {
      var th = document.createElement('th'); th.textContent = date; th.style.border = '1px solid #ddd'; th.style.padding = '6px'; trh.appendChild(th);
    });
    thead.appendChild(trh);
    table.appendChild(thead);

    var max = 0; dates.forEach(function (d) { if (mapping[d] && mapping[d].length > max) max = mapping[d].length; });

    var tbody = document.createElement('tbody');
    for (var row = 0; row < max; row++) {
      var tr = document.createElement('tr');
      dates.forEach(function (d) {
        var td = document.createElement('td'); td.style.border = '1px solid #eee'; td.style.padding = '6px';
        var times = mapping[d] || [];
        if (times[row]) {
          (function (date, slot) {
            var btn = document.createElement('button');
            btn.textContent = slot.time;
            btn.addEventListener('click', function () {
              var dateIso = date + 'T' + slot.time;
              var payload = {
                patientId: parseInt(patientId),
                dutyId: parseInt(slot.dutyId),
                startDate: dateIso
              };
              apiPost('/visits', payload)
                .then(function (resp) {
                  if (resp.ok) {
                    var container = document.getElementById('visit-slots-container');
                    if (container) container.innerHTML = '';
                  }
                  return handleFetchResponse(resp);
                })
                .catch(handleFetchError);
            });
            td.appendChild(btn);
          })(d, times[row]);
        }
        tr.appendChild(td);
      });
      tbody.appendChild(tr);
    }
    table.appendChild(tbody);
    container.appendChild(table);
    setOutput('Rendered available slots (' + dates.length + ' days).');
  }

  loadPatients();
  loadDoctors();
  // Expose a refresh function so other code (e.g. showView) can request fresh lists
  window.refreshVisitsView = function () {
    try { loadPatients(); loadDoctors(); }
    catch (e) { console.error('refreshVisitsView failed', e); }
  };
}
