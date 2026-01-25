/**
 * Main Application - Navigation and Initialization
 */

var PAGES = ['doctors', 'patients', 'examrooms', 'duties', 'visits'];

function renderNavbar() {
  var nav = document.getElementById('navbar');
  nav.innerHTML = '';

  // Initialize Database button
  var btnInit = document.createElement('button');
  btnInit.textContent = '🗄️ Initialize Database';
  btnInit.style.backgroundColor = '#28a745';
  btnInit.style.color = '#fff';
  btnInit.style.borderColor = '#28a745';
  btnInit.addEventListener('click', function () {
    if (confirm('Initialize database with sample data?')) {
      apiPost('/database/initialize', {})
        .then(function (response) {
          handleFetchResponse(response);
          // Re-render duties view to refresh selects
          renderDutiesView();
        })
        .catch(handleFetchError);
    }
  });
  nav.appendChild(btnInit);

  // Separator
  var separator = document.createElement('span');
  separator.style.marginRight = '20px';
  nav.appendChild(separator);

  // Page buttons
  PAGES.forEach(function (page) {
    var btn = document.createElement('button');
    btn.textContent = page.charAt(0).toUpperCase() + page.slice(1);
    btn.dataset.page = page;
    btn.addEventListener('click', function () {
      showView(page);
    });
    nav.appendChild(btn);
  });
}

function setActiveNavbarItem(page) {
  var buttons = document.querySelectorAll('#navbar button');
  buttons.forEach(function (btn) {
    if (btn.dataset.page === page) {
      btn.classList.add('active');
    } else {
      btn.classList.remove('active');
    }
  });
}

function renderAllViews() {
  renderDoctorsView();
  renderPatientsView();
  renderExamRoomsView();
  renderDutiesView();
  renderVisitsView();
}

function showView(page) {
  PAGES.forEach(function (p) {
    var el = document.getElementById('view-' + p);
    if (el) {
      el.style.display = (p === page) ? 'block' : 'none';
    }
  });
  setActiveNavbarItem(page);

  // If visits page was requested, refresh its lists (patients/doctors)
  if (page === 'visits' && typeof window.refreshVisitsView === 'function') {
    try { window.refreshVisitsView(); } catch (e) { console.error('Failed to refresh visits lists', e); }
  }
}

function init() {
  renderNavbar();
  renderAllViews();
  showView('doctors');
}

// Initialize when DOM is ready
document.addEventListener('DOMContentLoaded', init);
