/**
 * Patients View
 */
function renderPatientsView() {
    var view = document.getElementById('view-patients');
    view.innerHTML = '';

    var title = document.createElement('h2');
    title.textContent = 'Patients API';
    view.appendChild(title);

    // --- Load all patients ---
    var fsList = document.createElement('fieldset');
    var legendList = document.createElement('legend');
    legendList.textContent = 'Get all patients (GET /api/patients)';
    fsList.appendChild(legendList);

    var btnLoad = document.createElement('button');
    btnLoad.textContent = 'Load all patients';
    btnLoad.addEventListener('click', function () {
        apiGet('/patients')
            .then(handleFetchResponse)
            .catch(handleFetchError);
    });
    fsList.appendChild(btnLoad);
    view.appendChild(fsList);

    // --- Get patient by ID ---
    var fsGet = document.createElement('fieldset');
    var legendGet = document.createElement('legend');
    legendGet.textContent = 'Get patient by ID (GET /api/patients/{id})';
    fsGet.appendChild(legendGet);

    var labelGet = document.createElement('label');
    labelGet.textContent = 'Patient ID:';
    fsGet.appendChild(labelGet);

    var inputGet = document.createElement('input');
    inputGet.type = 'text';
    inputGet.placeholder = 'e.g. 1';
    fsGet.appendChild(inputGet);
    fsGet.appendChild(document.createElement('br'));

    var btnGet = document.createElement('button');
    btnGet.textContent = 'Get patient';
    btnGet.addEventListener('click', function () {
        var id = inputGet.value;
        if (!id) {
            alert('Please provide patient ID');
            return;
        }
        apiGet('/patients/' + encodeURIComponent(id))
            .then(handleFetchResponse)
            .catch(handleFetchError);
    });
    fsGet.appendChild(btnGet);
    view.appendChild(fsGet);

    // --- Delete patient by ID ---
    var fsDelete = document.createElement('fieldset');
    var legendDelete = document.createElement('legend');
    legendDelete.textContent = 'Delete patient by ID (DELETE /api/patients/{id})';
    fsDelete.appendChild(legendDelete);

    var labelDelete = document.createElement('label');
    labelDelete.textContent = 'Patient ID:';
    fsDelete.appendChild(labelDelete);

    var inputDelete = document.createElement('input');
    inputDelete.type = 'text';
    inputDelete.placeholder = 'e.g. 1';
    fsDelete.appendChild(inputDelete);
    fsDelete.appendChild(document.createElement('br'));

    var btnDelete = document.createElement('button');
    btnDelete.textContent = 'Delete patient';
    btnDelete.addEventListener('click', function () {
        var id = inputDelete.value;
        if (!id) {
            alert('Please provide patient ID');
            return;
        }
        apiDelete('/patients/' + encodeURIComponent(id))
            .then(handleFetchResponse)
            .catch(handleFetchError);
    });
    fsDelete.appendChild(btnDelete);
    view.appendChild(fsDelete);

    // --- Create patient (raw JSON) ---
    var fsCreate = document.createElement('fieldset');
    var legendCreate = document.createElement('legend');
    legendCreate.textContent = 'Create patient (POST /api/patients)';
    fsCreate.appendChild(legendCreate);

    var infoCreate = document.createElement('p');
    infoCreate.textContent = 'Paste JSON matching PatientRequest:';
    fsCreate.appendChild(infoCreate);

    var textarea = document.createElement('textarea');
    textarea.style.height = '95px';
    textarea.value = JSON.stringify({
        firstName: "Anna",
        lastName: "Nowak",
        pesel: "90010112345",
        address: "Warszawa, Marszałkowska 10, 00-001"
    }, null, 2);
    fsCreate.appendChild(textarea);
    fsCreate.appendChild(document.createElement('br'));

    var btnCreate = document.createElement('button');
    btnCreate.textContent = 'Create patient';
    btnCreate.addEventListener('click', function () {
        var raw = textarea.value;
        var payload;

        try {
            payload = JSON.parse(raw);
        } catch (e) {
            alert('Invalid JSON: ' + e);
            return;
        }

        apiPost('/patients', payload)
            .then(handleFetchResponse)
            .catch(handleFetchError);
    });
    fsCreate.appendChild(btnCreate);
    view.appendChild(fsCreate);
}
