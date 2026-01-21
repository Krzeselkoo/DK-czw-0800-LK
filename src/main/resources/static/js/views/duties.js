/**
 * Duties View
 */

// Helper function to format datetime-local for input
function formatDateTimeLocal(date) {
  var pad = function (n) { return n < 10 ? '0' + n : n; };
  return date.getFullYear() + '-' + pad(date.getMonth() + 1) + '-' + pad(date.getDate()) +
    'T' + pad(date.getHours()) + ':' + pad(date.getMinutes());
}

// Helper to populate select with options
function populateSelect(select, items, valueField, labelFn) {
  select.innerHTML = '';
  var defaultOption = document.createElement('option');
  defaultOption.value = '';
  defaultOption.textContent = items.length > 0 ? '-- Select --' : '-- No available items --';
  select.appendChild(defaultOption);

  items.forEach(function (item) {
    var option = document.createElement('option');
    option.value = item[valueField];
    option.textContent = labelFn(item);
    select.appendChild(option);
  });
}

function renderDutiesView() {
  var view = document.getElementById('view-duties');
  view.innerHTML = '';

  var title = document.createElement('h2');
  title.textContent = 'Duties API';
  view.appendChild(title);

  // --- Load all duties ---
  var fsList = document.createElement('fieldset');
  var legendList = document.createElement('legend');
  legendList.textContent = 'Get all duties (GET /api/duties)';
  fsList.appendChild(legendList);

  var btnLoad = document.createElement('button');
  btnLoad.textContent = 'Load all duties';
  btnLoad.addEventListener('click', function () {
    apiGet('/duties')
      .then(handleFetchResponse)
      .catch(handleFetchError);
  });
  fsList.appendChild(btnLoad);
  view.appendChild(fsList);

  // --- Get duty by ID ---
  var fsGet = document.createElement('fieldset');
  var legendGet = document.createElement('legend');
  legendGet.textContent = 'Get duty by ID (GET /api/duties/{id})';
  fsGet.appendChild(legendGet);

  var labelGet = document.createElement('label');
  labelGet.textContent = 'Duty ID:';
  fsGet.appendChild(labelGet);

  var inputGet = document.createElement('input');
  inputGet.type = 'text';
  inputGet.placeholder = 'e.g. 1';
  fsGet.appendChild(inputGet);
  fsGet.appendChild(document.createElement('br'));

  var btnGet = document.createElement('button');
  btnGet.textContent = 'Get duty';
  btnGet.addEventListener('click', function () {
    var id = inputGet.value;
    if (!id) {
      alert('Please provide duty ID');
      return;
    }
    apiGet('/duties/' + encodeURIComponent(id))
      .then(handleFetchResponse)
      .catch(handleFetchError);
  });
  fsGet.appendChild(btnGet);
  view.appendChild(fsGet);

  // --- Delete duty by ID ---
  var fsDelete = document.createElement('fieldset');
  var legendDelete = document.createElement('legend');
  legendDelete.textContent = 'Delete duty by ID (DELETE /api/duties/{id})';
  fsDelete.appendChild(legendDelete);

  var labelDelete = document.createElement('label');
  labelDelete.textContent = 'Duty ID:';
  fsDelete.appendChild(labelDelete);

  var inputDelete = document.createElement('input');
  inputDelete.type = 'text';
  inputDelete.placeholder = 'e.g. 1';
  fsDelete.appendChild(inputDelete);
  fsDelete.appendChild(document.createElement('br'));

  var btnDelete = document.createElement('button');
  btnDelete.textContent = 'Delete duty';
  btnDelete.addEventListener('click', function () {
    var id = inputDelete.value;
    if (!id) {
      alert('Please provide duty ID');
      return;
    }
    apiDelete('/duties/' + encodeURIComponent(id))
      .then(handleFetchResponse)
      .catch(handleFetchError);
  });
  fsDelete.appendChild(btnDelete);
  view.appendChild(fsDelete);

  // --- Create duty with date picker and available doctors/rooms ---
  var fsCreate = document.createElement('fieldset');
  var legendCreate = document.createElement('legend');
  legendCreate.textContent = 'Create duty (POST /api/duties) - Step by step';
  fsCreate.appendChild(legendCreate);

  // Step 1: Date/Time selection
  var step1 = document.createElement('div');
  step1.innerHTML = '<strong>Step 1:</strong> Select duty date and time range';
  fsCreate.appendChild(step1);

  var dateLabel = document.createElement('label');
  dateLabel.textContent = 'Date:';
  fsCreate.appendChild(dateLabel);

  var dateInput = document.createElement('input');
  dateInput.type = 'date';
  var today = new Date();
  dateInput.value = today.toISOString().split('T')[0];
  fsCreate.appendChild(dateInput);
  fsCreate.appendChild(document.createElement('br'));

  var startLabel = document.createElement('label');
  startLabel.textContent = 'Start time:';
  fsCreate.appendChild(startLabel);

  var startTimeInput = document.createElement('input');
  startTimeInput.type = 'time';
  startTimeInput.value = '08:00';
  fsCreate.appendChild(startTimeInput);
  fsCreate.appendChild(document.createElement('br'));

  var endLabel = document.createElement('label');
  endLabel.textContent = 'End time:';
  fsCreate.appendChild(endLabel);

  var endTimeInput = document.createElement('input');
  endTimeInput.type = 'time';
  endTimeInput.value = '16:00';
  fsCreate.appendChild(endTimeInput);
  fsCreate.appendChild(document.createElement('br'));

  // Step 2: Available rooms and doctors
  var step2 = document.createElement('div');
  step2.innerHTML = '<strong>Step 2:</strong> Select available room and doctor (auto-loads when date changes)';
  step2.style.marginTop = '15px';
  fsCreate.appendChild(step2);

  // Doctor select
  var doctorLabel = document.createElement('label');
  doctorLabel.textContent = 'Available Doctor:';
  fsCreate.appendChild(doctorLabel);

  var doctorSelect = document.createElement('select');
  doctorSelect.id = 'duty-doctor-select';
  fsCreate.appendChild(doctorSelect);
  fsCreate.appendChild(document.createElement('br'));

  // Room select
  var roomLabel = document.createElement('label');
  roomLabel.textContent = 'Available Room:';
  fsCreate.appendChild(roomLabel);

  var roomSelect = document.createElement('select');
  roomSelect.id = 'duty-room-select';
  fsCreate.appendChild(roomSelect);
  fsCreate.appendChild(document.createElement('br'));

  // Load available doctors and rooms function
  function loadAvailableOptions() {
    var date = dateInput.value;
    var startTime = startTimeInput.value;
    var endTime = endTimeInput.value;

    if (!date || !startTime || !endTime) return;

    var from = date + 'T' + startTime;
    var to = date + 'T' + endTime;

    // Load available doctors
    apiGet('/doctors/duties?from=' + encodeURIComponent(from) + '&to=' + encodeURIComponent(to))
      .then(function (response) {
        return response.json();
      })
      .then(function (doctors) {
        populateSelect(doctorSelect, doctors, 'id', function (d) {
          return d.firstName + ' ' + d.lastName + ' (' + d.specialization + ')';
        });
      })
      .catch(function (err) {
        console.error('Failed to load doctors:', err);
        populateSelect(doctorSelect, [], 'id', function () { return ''; });
      });

    // Load available rooms
    apiGet('/exam_rooms/duties?from=' + encodeURIComponent(from) + '&to=' + encodeURIComponent(to))
      .then(function (response) {
        return response.json();
      })
      .then(function (rooms) {
        populateSelect(roomSelect, rooms, 'id', function (r) {
          return r.roomCode + ' (' + r.roomType + ')';
        });
      })
      .catch(function (err) {
        console.error('Failed to load rooms:', err);
        populateSelect(roomSelect, [], 'id', function () { return ''; });
      });
  }

  // Load on date or time change
  dateInput.addEventListener('change', loadAvailableOptions);
  startTimeInput.addEventListener('change', loadAvailableOptions);
  endTimeInput.addEventListener('change', loadAvailableOptions);

  // Initial load
  loadAvailableOptions();

  // Step 3: Create button
  var step3 = document.createElement('div');
  step3.innerHTML = '<strong>Step 3:</strong> Create the duty';
  step3.style.marginTop = '15px';
  fsCreate.appendChild(step3);

  var btnCreateDuty = document.createElement('button');
  btnCreateDuty.textContent = 'Create duty';
  btnCreateDuty.addEventListener('click', function () {
    var date = dateInput.value;
    var startTime = startTimeInput.value;
    var endTime = endTimeInput.value;
    var doctorId = doctorSelect.value;
    var roomId = roomSelect.value;

    if (!date || !startTime || !endTime) {
      alert('Please provide date and time range');
      return;
    }
    if (!doctorId) {
      alert('Please select a doctor');
      return;
    }
    if (!roomId) {
      alert('Please select a room');
      return;
    }

    var startDateTime = date + 'T' + startTime;
    var endDateTime = date + 'T' + endTime;

    var payload = {
      doctorId: parseInt(doctorId),
      examRoomId: parseInt(roomId),
      fromDate: startDateTime,
      toDate: endDateTime
    };

    apiPost('/duties', payload)
      .then(handleFetchResponse)
      .catch(handleFetchError);
  });
  fsCreate.appendChild(btnCreateDuty);
  view.appendChild(fsCreate);

  // --- Create duty (raw JSON) ---
  var fsCreateRaw = document.createElement('fieldset');
  var legendCreateRaw = document.createElement('legend');
  legendCreateRaw.textContent = 'Create duty - Raw JSON (POST /api/duties)';
  fsCreateRaw.appendChild(legendCreateRaw);

  var infoCreateRaw = document.createElement('p');
  infoCreateRaw.textContent = 'Paste JSON matching DutyRequest:';
  fsCreateRaw.appendChild(infoCreateRaw);

  var now = new Date();
  var startDefault = new Date(now);
  startDefault.setHours(8, 0, 0, 0);
  var endDefault = new Date(now);
  endDefault.setHours(16, 0, 0, 0);

  var textareaRaw = document.createElement('textarea');
  textareaRaw.style.height = '95px';
  textareaRaw.value = JSON.stringify({
    doctorId: 1,
    examRoomId: 1,
    fromDate: formatDateTimeLocal(startDefault),
    toDate: formatDateTimeLocal(endDefault)
  }, null, 2);
  fsCreateRaw.appendChild(textareaRaw);
  fsCreateRaw.appendChild(document.createElement('br'));

  var btnCreateRaw = document.createElement('button');
  btnCreateRaw.textContent = 'Create duty';
  btnCreateRaw.addEventListener('click', function () {
    var raw = textareaRaw.value;
    var payload;

    try {
      payload = JSON.parse(raw);
    } catch (e) {
      alert('Invalid JSON: ' + e);
      return;
    }

    apiPost('/duties', payload)
      .then(handleFetchResponse)
      .catch(handleFetchError);
  });
  fsCreateRaw.appendChild(btnCreateRaw);
  view.appendChild(fsCreateRaw);
}
