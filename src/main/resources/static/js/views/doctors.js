/**
 * Doctors View
 */
function renderDoctorsView() {
  var view = document.getElementById('view-doctors');
  view.innerHTML = '';

  var title = document.createElement('h2');
  title.textContent = 'Doctors API';
  view.appendChild(title);

  // --- Load all doctors ---
  var fsList = document.createElement('fieldset');
  var legendList = document.createElement('legend');
  legendList.textContent = 'Get all doctors (GET /api/doctors)';
  fsList.appendChild(legendList);

  var btnLoad = document.createElement('button');
  btnLoad.textContent = 'Load all doctors';
  btnLoad.addEventListener('click', function () {
    apiGet('/doctors')
      .then(handleFetchResponse)
      .catch(handleFetchError);
  });
  fsList.appendChild(btnLoad);
  view.appendChild(fsList);

  // --- Get doctor by ID ---
  var fsGet = document.createElement('fieldset');
  var legendGet = document.createElement('legend');
  legendGet.textContent = 'Get doctor by ID (GET /api/doctors/{id})';
  fsGet.appendChild(legendGet);

  var labelGet = document.createElement('label');
  labelGet.textContent = 'Doctor ID:';
  fsGet.appendChild(labelGet);

  var inputGet = document.createElement('input');
  inputGet.type = 'text';
  inputGet.placeholder = 'e.g. 1';
  fsGet.appendChild(inputGet);
  fsGet.appendChild(document.createElement('br'));

  var btnGet = document.createElement('button');
  btnGet.textContent = 'Get doctor';
  btnGet.addEventListener('click', function () {
    var id = inputGet.value;
    if (!id) {
      alert('Please provide doctor ID');
      return;
    }
    apiGet('/doctors/' + encodeURIComponent(id))
      .then(handleFetchResponse)
      .catch(handleFetchError);
  });
  fsGet.appendChild(btnGet);

  // Button to load doctor with duties
  var btnGetWithDuties = document.createElement('button');
  btnGetWithDuties.textContent = 'Get doctor with duties';
  btnGetWithDuties.addEventListener('click', function () {
    var id = inputGet.value;
    if (!id) {
      alert('Please provide doctor ID');
      return;
    }

    // First check if doctor exists
    apiGet('/doctors/' + encodeURIComponent(id))
      .then(function (response) {
        if (!response.ok) {
          return response.text().then(function (text) {
            setOutput('Status: ' + response.status + '\n\n' + text);
            throw new Error('Doctor not found');
          });
        }
        return response.json();
      })
      .then(function (doctor) {
        // Doctor exists, now get duties
        return apiGet('/duties/by-doctor/' + encodeURIComponent(id))
          .then(function (r) { return r.json(); })
          .then(function (duties) {
            var output = 'Doctor Details:\n';
            output += JSON.stringify(doctor, null, 2);
            output += '\n\nDuties (' + duties.length + '):\n';

            if (duties.length === 0) {
              output += 'No duties assigned.';
            } else {
              duties.forEach(function (duty) {
                output += '- Duty #' + duty.dutyId + ': Room ID ' + duty.examRoomId;
                output += ' | From: ' + duty.fromDate + ' To: ' + duty.toDate + '\n';
              });
            }
            setOutput(output);
          });
      })
      .catch(function (err) {
        if (err.message !== 'Doctor not found') {
          handleFetchError(err);
        }
      });
  });
  fsGet.appendChild(btnGetWithDuties);
  view.appendChild(fsGet);

  // --- Delete doctor by ID ---
  var fsDelete = document.createElement('fieldset');
  var legendDelete = document.createElement('legend');
  legendDelete.textContent = 'Delete doctor by ID (DELETE /api/doctors/{id})';
  fsDelete.appendChild(legendDelete);

  var labelDelete = document.createElement('label');
  labelDelete.textContent = 'Doctor ID:';
  fsDelete.appendChild(labelDelete);

  var inputDelete = document.createElement('input');
  inputDelete.type = 'text';
  inputDelete.placeholder = 'e.g. 1';
  fsDelete.appendChild(inputDelete);
  fsDelete.appendChild(document.createElement('br'));

  var btnDelete = document.createElement('button');
  btnDelete.textContent = 'Delete doctor';
  btnDelete.addEventListener('click', function () {
    var id = inputDelete.value;
    if (!id) {
      alert('Please provide doctor ID');
      return;
    }
    apiDelete('/doctors/' + encodeURIComponent(id))
      .then(handleFetchResponse)
      .catch(handleFetchError);
  });
  fsDelete.appendChild(btnDelete);
  view.appendChild(fsDelete);

  // --- Create doctor (raw JSON) ---
  var fsCreate = document.createElement('fieldset');
  var legendCreate = document.createElement('legend');
  legendCreate.textContent = 'Create doctor (POST /api/doctors)';
  fsCreate.appendChild(legendCreate);

  var infoCreate = document.createElement('p');
  infoCreate.textContent = 'Paste JSON matching DoctorRequest:';
  fsCreate.appendChild(infoCreate);

  var textarea = document.createElement('textarea');
  textarea.style.height = '110px';
  textarea.value = JSON.stringify({
    firstName: "Jan",
    lastName: "Kowalski",
    pesel: "80010112345",
    specialization: "OTOLARYNGOLOGIST",
    address: "Krakow, Jana Matejki 34, 31-330"
  }, null, 2);
  fsCreate.appendChild(textarea);
  fsCreate.appendChild(document.createElement('br'));

  var infoSpec = document.createElement('p');
  infoSpec.innerHTML = '<small><strong>Specializations:</strong> OTOLARYNGOLOGIST, OPHTHALMOLOGIST, NEUROLOGIST, PSYCHOLOGIST</small>';
  fsCreate.appendChild(infoSpec);

  var btnCreate = document.createElement('button');
  btnCreate.textContent = 'Create doctor';
  btnCreate.addEventListener('click', function () {
    var raw = textarea.value;
    var payload;

    try {
      payload = JSON.parse(raw);
    } catch (e) {
      alert('Invalid JSON: ' + e);
      return;
    }

    apiPost('/doctors', payload)
      .then(handleFetchResponse)
      .catch(handleFetchError);
  });
  fsCreate.appendChild(btnCreate);
  view.appendChild(fsCreate);
}
