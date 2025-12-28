/**
 * API helper functions
 */
var API_BASE = '/api';

function setOutput(text) {
    var out = document.getElementById('output');
    out.textContent = text;
}

function handleFetchResponse(response) {
    var statusLine = 'Status: ' + response.status + ' ' + response.statusText + '\n\n';
    return response.text().then(function (body) {
        if (!body) {
            body = '<empty body>';
        }
        setOutput(statusLine + body);
    }).catch(function (err) {
        setOutput(statusLine + 'Error reading body: ' + err);
    });
}

function handleFetchError(error) {
    setOutput('Network error or CORS problem:\n' + error);
}

function apiGet(endpoint) {
    return fetch(API_BASE + endpoint, {
        method: 'GET',
        headers: { 'Accept': 'application/json' }
    });
}

function apiPost(endpoint, payload) {
    return fetch(API_BASE + endpoint, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        },
        body: JSON.stringify(payload)
    });
}

function apiDelete(endpoint) {
    return fetch(API_BASE + endpoint, {
        method: 'DELETE'
    });
}
