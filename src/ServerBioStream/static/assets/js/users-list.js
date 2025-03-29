
//Function to get the csrf token of Django
function getCsrfToken() {
  let csrfToken = null;
  const cookies = document.cookie.split(';');
  for (let cookie of cookies) {
      const [name, value] = cookie.split('=');
      if (name.trim() === 'csrftoken') {
      csrfToken = value;
      break;
      }
  }
  return csrfToken;
}

/*
    Table management
*/

// Store the pagination settings 
let paginationConfigs = {};

// Function to display a particular page of a filtered table
window.showPage = function(page, tableIdentifier) {
  let tableConfig = paginationConfigs[tableIdentifier];
  if (!tableConfig) return;

  let visibleRows = tableConfig.filteredRows;
  let start = (page - 1) * tableConfig.rowsPerPage;
  let end = start + tableConfig.rowsPerPage;

  [...tableConfig.tbody.rows].forEach(row => row.style.display = 'none');

  visibleRows.slice(start, end).forEach(row => {
    row.style.display = "table-row";
  });

  tableConfig.currentPage = page;
  updatePagination(tableIdentifier);
};

// Function to update pagination controls based on filtered rows
window.updatePagination = function(tableIdentifier) {
  let tableConfig = paginationConfigs[tableIdentifier];
  if (!tableConfig) return;

  let totalRows = tableConfig.filteredRows.length;
  let lastPage = Math.ceil(totalRows / tableConfig.rowsPerPage);
  let currentPage = tableConfig.currentPage;
  let paginationHTML = "";

  if (currentPage > 1) {
    paginationHTML += `<li class='page-item'><a class='page-link' href='#' onclick='showPage(1, "${tableIdentifier}")'>&lt;</a></li>`;
  }

  if (currentPage > 2) {
    paginationHTML += `<li class='page-item'><a class='page-link' href='#' onclick='showPage(${currentPage - 1}, "${tableIdentifier}")'>${currentPage - 1}</a></li>`;
  }

  paginationHTML += `<li class='page-item active'><a class='page-link' href='#'>${currentPage}</a></li>`;

  if (currentPage < lastPage - 1) {
    paginationHTML += `<li class='page-item'><a class='page-link' href='#' onclick='showPage(${currentPage + 1}, "${tableIdentifier}")'>${currentPage + 1}</a></li>`;
  }

  if (currentPage < lastPage) {
    paginationHTML += `<li class='page-item'><a class='page-link' href='#' onclick='showPage(${lastPage}, "${tableIdentifier}")'>&gt;</a></li>`;
  }

  tableConfig.pagination.innerHTML = paginationHTML;
};

// Initialize table pagination and filtering
function setupPagination(tableId, tbodyId, searchInputId, rowsPerPageId, paginationId) {
  const tbody = document.getElementById(tbodyId);
  const pagination = document.getElementById(paginationId);
  const rowsPerPageElement = document.getElementById(rowsPerPageId);
  const searchInput = document.getElementById(searchInputId);

  if (!tbody || !pagination || !rowsPerPageElement || !searchInput) {
    console.warn(`Some elements of the table ${tableId} were not found in the DOM.`);
    return;
  }

  let currentPage = 1;
  let rowsPerPage = parseInt(rowsPerPageElement.value);

  paginationConfigs[tableId] = {
    currentPage,
    rowsPerPage,
    tbody,
    pagination,
    filteredRows: [...tbody.rows] // Initially, all rows are visible
  };

  // Apply filtering based on input
  function applyFilter() {
    let filter = searchInput.value.toLowerCase();
    paginationConfigs[tableId].filteredRows = [...tbody.rows].filter(row => {
      return Array.from(row.cells).some(cell => 
        cell.innerText.toLowerCase().includes(filter)
      );
    });
    showPage(1, tableId);
  }

  // Search input event
  searchInput.addEventListener("input", applyFilter);

  // Rows per page input event
  rowsPerPageElement.addEventListener("change", function() {
    paginationConfigs[tableId].rowsPerPage = parseInt(this.value);
    applyFilter();
  });

  // Initialize the pagination
  applyFilter();
}

  // Configure a sortable table by columns
  function setupSortableTable(tableId) {
    let table = document.getElementById(tableId);
    let headers = table.querySelectorAll("th");
    headers.forEach((header, index) => {
      header.style.cursor = "pointer";
      header.addEventListener("click", function () {
        sortTable(tableId, index);
      });
    });
  }

// Improved function to sort table by column
function sortTable(tableId, columnIndex) {

  let table = document.getElementById(tableId);
  let tbody = table.querySelector("tbody");
  let rows = Array.from(tbody.rows);
  let ascending = table.dataset.sortOrder !== "asc";

  rows.sort((a, b) => {
    let valA = a.cells[columnIndex].innerText.trim();
    let valB = b.cells[columnIndex].innerText.trim();

    // Attempt to parse values explicitly as dates 
    let dateA = Date.parse(valA);
    let dateB = Date.parse(valB);

    if (!isNaN(dateA) && !isNaN(dateB)) {
      return ascending ? dateA - dateB : dateB - dateA;
    }

    // Attempt to parse explicitly as numbers (removing commas as thousand separators)
    let numA = parseFloat(valA.replace(/,/g, ''));
    let numB = parseFloat(valB.replace(/,/g, ''));

    if (!isNaN(numA) && !isNaN(numB)) {
      return ascending ? numA - numB : numB - numA;
    }

    // Fallback to alphabetical (textual) sorting with numeric comparison enabled
    return ascending
      ? valA.localeCompare(valB, undefined, { numeric: true, sensitivity: 'base' })
      : valB.localeCompare(valA, undefined, { numeric: true, sensitivity: 'base' });
  });

  tbody.innerHTML = "";
  rows.forEach(row => tbody.appendChild(row));

  table.dataset.sortOrder = ascending ? "asc" : "desc";
  showPage(1, tableId);
}


// Function to manage the deletion of a user
function handleDeleteUser(event) {

  const targetButton = event.target.closest('.delete-button');

  // If you do not click a delete button, exit
  if (!targetButton) return;

  const userUsername = targetButton.getAttribute('data-id');
  const url = '/delete-user/';

  Swal.fire({
    title: 'Are you sure?',
    text: "You won't be able to revert this!",
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: 'Yes, delete it!',
    cancelButtonText: 'Cancel'
  }).then((result) => {
    if (result.isConfirmed) {
      fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'X-CSRFToken': getCsrfToken()
        },
        body: JSON.stringify({ "username": userUsername })
      })
        .then(response => response.json())
        .then(data => {
          if (data.status === 'success') {
            Swal.fire(
              'Deleted!',
              'The user has been deleted.',
              'success'
            ).then(() => {
              window.location.reload();
            });
          } else {
            Swal.fire('Error!', data.message, 'error');
          }
        })
        .catch(error => {
          let errorMessage = error.message || 'There was an issue deleting this user.';
          Swal.fire('Error!', errorMessage, 'error');
        });
    }
  });
}

// Configuration when loading the html template
document.addEventListener("DOMContentLoaded", function () {
  setupPagination("userTable", "userTableBody", "searchInput", "rowsPerPage", "pagination");

  setupSortableTable("userTable");

  const userTableBody = document.getElementById('userTableBody');
  userTableBody.addEventListener('click', handleDeleteUser);


});
