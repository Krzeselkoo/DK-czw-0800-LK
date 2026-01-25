/**
 * Exam Rooms View
 */
function renderExamRoomsView() {
  var view = document.getElementById('view-examrooms');
  view.innerHTML = '';

  var title = document.createElement('h2');
  title.textContent = 'Exam Rooms API';
  view.appendChild(title);

  // --- Load all exam rooms ---
  var fsList = document.createElement('fieldset');
  var legendList = document.createElement('legend');
  legendList.textContent = 'Get all exam rooms (GET /api/exam_rooms)';
  fsList.appendChild(legendList);

  var btnLoad = document.createElement('button');
  btnLoad.textContent = 'Load all exam rooms';
  btnLoad.addEventListener('click', function () {
    apiGet('/exam_rooms')
      .then(handleFetchResponse)
      .catch(handleFetchError);
  });
  fsList.appendChild(btnLoad);
  view.appendChild(fsList);

  // --- Get exam room by ID ---
  var fsGet = document.createElement('fieldset');
  var legendGet = document.createElement('legend');
  legendGet.textContent = 'Get exam room by ID (GET /api/exam_rooms/{id})';
  fsGet.appendChild(legendGet);

  var labelGet = document.createElement('label');
  labelGet.textContent = 'Exam Room ID:';
  fsGet.appendChild(labelGet);

  var inputGet = document.createElement('input');
  inputGet.type = 'text';
  inputGet.placeholder = 'e.g. 1';
  fsGet.appendChild(inputGet);
  fsGet.appendChild(document.createElement('br'));

  var btnGet = document.createElement('button');
  btnGet.textContent = 'Get exam room';
  btnGet.addEventListener('click', function () {
    var id = inputGet.value;
    if (!id) {
      alert('Please provide exam room ID');
      return;
    }
    apiGet('/exam_rooms/' + encodeURIComponent(id))
      .then(handleFetchResponse)
      .catch(handleFetchError);
  });
  fsGet.appendChild(btnGet);
  view.appendChild(fsGet);

  // --- Get exam room by ID with duties ---
  var fsGetDuties = document.createElement('fieldset');
  var legendGetDuties = document.createElement('legend');
  legendGetDuties.textContent = 'Get exam room with all duties';
  fsGetDuties.appendChild(legendGetDuties);

  var labelGetDuties = document.createElement('label');
  labelGetDuties.textContent = 'Exam Room ID:';
  fsGetDuties.appendChild(labelGetDuties);

  var inputGetDuties = document.createElement('input');
  inputGetDuties.type = 'text';
  inputGetDuties.placeholder = 'e.g. 1';
  fsGetDuties.appendChild(inputGetDuties);
  fsGetDuties.appendChild(document.createElement('br'));

  var btnGetDuties = document.createElement('button');
  btnGetDuties.textContent = 'Get exam room with duties';
  btnGetDuties.addEventListener('click', function () {
    var id = inputGetDuties.value;
    if (!id) {
      alert('Please provide exam room ID');
      return;
    }

    // First check if room exists
    apiGet('/exam_rooms/' + encodeURIComponent(id))
      .then(function (response) {
        if (!response.ok) {
          return response.text().then(function (text) {
            setOutput('Status: ' + response.status + '\n\n' + text);
            throw new Error('Room not found');
          });
        }
        return response.json();
      })
      .then(function (room) {
        // Room exists, now get duties
        return apiGet('/duties/by-room/' + encodeURIComponent(id))
          .then(function (r) { return r.json(); })
          .then(function (duties) {
            var output = 'Exam Room Details:\n';
            output += JSON.stringify(room, null, 2);
            output += '\n\nDuties in this room (' + duties.length + '):\n';

            if (duties.length === 0) {
              output += 'No duties assigned to this room.';
            } else {
              duties.forEach(function (duty) {
                output += '- Duty #' + duty.dutyId + ': Doctor ID ' + duty.doctorId;
                output += ' | From: ' + duty.fromDate + ' To: ' + duty.toDate + '\n';
              });
            }
            setOutput(output);
          });
      })
      .catch(function (err) {
        if (err.message !== 'Room not found') {
          handleFetchError(err);
        }
      });
  });
  fsGetDuties.appendChild(btnGetDuties);
  view.appendChild(fsGetDuties);

  // --- Delete exam room by ID ---
  var fsDelete = document.createElement('fieldset');
  var legendDelete = document.createElement('legend');
  legendDelete.textContent = 'Delete exam room by ID (DELETE /api/exam_rooms/{id})';
  fsDelete.appendChild(legendDelete);

  var labelDelete = document.createElement('label');
  labelDelete.textContent = 'Exam Room ID:';
  fsDelete.appendChild(labelDelete);

  var inputDelete = document.createElement('input');
  inputDelete.type = 'text';
  inputDelete.placeholder = 'e.g. 1';
  fsDelete.appendChild(inputDelete);
  fsDelete.appendChild(document.createElement('br'));

  var infoDelete = document.createElement('p');
  infoDelete.textContent = 'Note: You cannot delete a room that has scheduled duties.';
  infoDelete.style.color = 'red';
  infoDelete.style.fontSize = '0.9em';
  fsDelete.appendChild(infoDelete);

  var btnDelete = document.createElement('button');
  btnDelete.textContent = 'Delete exam room';
  btnDelete.addEventListener('click', function () {
    var id = inputDelete.value;
    if (!id) {
      alert('Please provide exam room ID');
      return;
    }
    apiDelete('/exam_rooms/' + encodeURIComponent(id))
      .then(handleFetchResponse)
      .catch(handleFetchError);
  });
  fsDelete.appendChild(btnDelete);
  view.appendChild(fsDelete);

  // --- Create exam room ---
  var fsCreate = document.createElement('fieldset');
  var legendCreate = document.createElement('legend');
  legendCreate.textContent = 'Create exam room (POST /api/exam_rooms)';
  fsCreate.appendChild(legendCreate);

  var infoCreate = document.createElement('p');
  infoCreate.textContent = 'Paste JSON matching ExamRoomRequest:';
  fsCreate.appendChild(infoCreate);

  var textarea = document.createElement('textarea');
  textarea.style.height = '65px';
  textarea.value = JSON.stringify({
    roomCode: "A-101",
    roomType: "GENERAL"
  }, null, 2);
  fsCreate.appendChild(textarea);
  fsCreate.appendChild(document.createElement('br'));

  var infoTypes = document.createElement('p');
  infoTypes.innerHTML = '<small><strong>Room types:</strong> GENERAL, ULTRASOUND, SURGERY</small>';
  fsCreate.appendChild(infoTypes);

  var btnCreate = document.createElement('button');
  btnCreate.textContent = 'Create exam room';
  btnCreate.addEventListener('click', function () {
    var raw = textarea.value;
    var payload;

    try {
      payload = JSON.parse(raw);
    } catch (e) {
      alert('Invalid JSON: ' + e);
      return;
    }

    apiPost('/exam_rooms', payload)
      .then(handleFetchResponse)
      .catch(handleFetchError);
  });
  fsCreate.appendChild(btnCreate);
  view.appendChild(fsCreate);
}
