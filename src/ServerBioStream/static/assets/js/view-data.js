
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
    Table (experiments and measurements) management
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

// Function to load the measurements (in measurements table)
// of the selected experiments
function fetchMeasurementsForSelectedExperiments() {

    const measurementsCountElement = document.getElementById('measurements-count');

    let selectedExperimentsArray = [...document.querySelectorAll(".experiment-checkbox:checked")].map(cb => cb.value);
    let measurementsTableBody = document.getElementById("measurementsTableBody");
    measurementsTableBody.innerHTML = "";

    if (selectedExperimentsArray.length === 0) return;
    $.ajax({
      url: "/fetch-measurements/",
      type: "GET",
      data: { experiments: selectedExperimentsArray },
      dataType: "json",
      success: function (data) {
        let table = $("#measurementsTable")
        if (data.measurements.length > 0) {
          data.measurements.forEach(meas => {
            let row = document.createElement("tr");
            row.innerHTML = `
                            <td>${meas.experiment}</td>
                            <td>${meas.timestamp}</td>
                            <td>${meas.type}</td>
                            <td>${meas.value}</td>
                        `;
            measurementsTableBody.appendChild(row);
          });

          measurementsCountElement.textContent = data.measurements.length;
          setupPagination("measurementsTable", "measurementsTableBody", "searchInputMeasurements", "rowsPerPageMeasurements", "paginationMeasurements");
        } else { }
      },
      error: function (error) {
        console.error("Error fetching measurements:", error);
      }
    });
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
  
    rows.sort((a, b) => a.cells[columnIndex].textContent.trim().localeCompare(b.cells[columnIndex].textContent.trim(), undefined, { numeric: true }));
    if (!ascending) rows.reverse();
  
    tbody.innerHTML = "";
    rows.forEach(row => tbody.appendChild(row));
  
    table.dataset.sortOrder = ascending ? "asc" : "desc";
    showPage(1, tableId);
  }
  


function refreshExperimentsTable() {
    location.reload();
}

function refreshMeasurementsTable() {
    fetchMeasurementsForSelectedExperiments();
}

// Function to manage the deletion of an experiment button
function handleDeleteExperiment(event) {
    const targetButton = event.target.closest('.delete-button');
  
    // If you do not click a delete button, exit
    if (!targetButton) return;

    const experimentId = targetButton.getAttribute('data-id');
    const url = '/delete-experiment/';

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
          body: JSON.stringify({ "experimentId": experimentId })
        })
          .then(response => response.json())
          .then(data => {
            if (data.status === 'success') {
              Swal.fire(
                'Deleted!',
                'Your experiment has been deleted.',
                'success'
              ).then(() => {
                window.location.reload();
              });
            } else {
              Swal.fire('Error!', data.message, 'error');
            }
          })
          .catch(error => {
            let errorMessage = error.message || 'There was an issue deleting the experiment case.';
            Swal.fire('Error!', errorMessage, 'error');
          });
      }
    });
  }
  
// Clear and re-initialize table pagination, sorting, and filtering after updating content dynamically
function reinitializeTable(tableId, tbodyId, searchInputId, rowsPerPageId, paginationId) {
    if (paginationConfigs[tableId]) delete paginationConfigs[tableId];
    setupPagination(tableId, tbodyId, searchInputId, rowsPerPageId, paginationId);
    setupSortableTable(tableId);
  }
  
// Configuration when loading the html template
document.addEventListener("DOMContentLoaded", function () {
    setupPagination("experimentsTable", "experimentsTableBody", "searchInputExperiments", "rowsPerPageExperiments", "paginationExperiments");
    setupPagination("measurementsTable", "measurementsTableBody", "searchInputMeasurements", "rowsPerPageMeasurements", "paginationMeasurements");

    setupSortableTable("experimentsTable");
    setupSortableTable("measurementsTable");

    // Checkbox to select an experiment and load its measurements
    document.querySelectorAll(".experiment-checkbox").forEach(checkbox => {
      checkbox.addEventListener("change", fetchMeasurementsForSelectedExperiments);
    });


    // Button to delete an experiment
    const experimentsTableBody = document.getElementById('experimentsTableBody');
    experimentsTableBody.addEventListener('click', handleDeleteExperiment);
  });

/*
    Data export
*/
  //Manage the export experiments button
function export_experiments() {
    Swal.fire({
      title: 'Select format export',
      input: 'select',
      inputOptions: {
        'csv': 'CSV',
        'excel': 'Excel',
        'pdf': 'PDF'
      },
      inputPlaceholder: 'Select a format',
      showCancelButton: true
    }).then((result) => {
      if (result.isConfirmed) {
        let format = result.value;
        exportTableData("experimentsTable", [1, 2, 3, 4], format);
      }
    });
  }

//Manage the export measurements button
function export_measurements() {
    Swal.fire({
      title: 'Select format export',
      input: 'select',
      inputOptions: {
        'csv': 'CSV',
        'excel': 'Excel',
        'pdf': 'PDF'
      },
      inputPlaceholder: 'Select a format',
      showCancelButton: true
    }).then((result) => {
      if (result.isConfirmed) {
        let format = result.value;
        exportTableData("measurementsTable", [0, 1, 2, 3], format);
      }
    });
  }

// Controller function to export data from any table
function exportTableData(tableId, columnsToExport, format) {
    let table = document.getElementById(tableId);
    if (!table) {
      console.error("Table not found.");
      return;
    }
        if (format === 'csv') {
        exportTableToCSV(table, columnsToExport);
        } else if (format === 'excel') {
        exportTableToExcel(table, columnsToExport);
        } else if (format === 'pdf') {
        exportTableToPDF(table, columnsToExport);
        }
}

// Function to export table data to CSV
function exportTableToCSV(table, columnsToExport) {
    let rows = table.querySelectorAll("tr");
    let csv = [];
  
    // Extract headers (first row) based on specified columns
    let headers = columnsToExport.map(index => `"${rows[0].cells[index].textContent.trim()}"`);
    csv.push(headers.join(','));
  
    // Extract data from remaining rows (skip first row - headers)
    for (let i = 1; i < rows.length; i++) {
      let rowData = columnsToExport.map(index => `"${rows[i].cells[index].textContent.trim()}"`).join(',');
      csv.push(rowData);
    }
  
    let csvFile = new Blob([csv.join('\n')], { type: 'text/csv' });
    let downloadLink = document.createElement('a');
    downloadLink.download = `${table.id}_data.csv`;
    downloadLink.href = window.URL.createObjectURL(csvFile);
    downloadLink.style.display = 'none';
    document.body.appendChild(downloadLink);
    downloadLink.click();
    document.body.removeChild(downloadLink);
}

// Function to export specific columns from a table to Excel
function exportTableToExcel(table, columnsToExport) {
    let rows = table.querySelectorAll("tr");
    let data = [];

    // Add headers
    let headers = columnsToExport.map(index => rows[0].cells[index].textContent.trim());
    data.push(headers);

    // Add rows
    for (let i = 1; i < rows.length; i++) {
        let rowData = columnsToExport.map(index => rows[i].cells[index].textContent.trim());
        data.push(rowData);
    }

    // Create worksheet and workbook
    let worksheet = XLSX.utils.aoa_to_sheet(data);
    let workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, "Sheet1");

    // Export to .xlsx
    XLSX.writeFile(workbook, `${table.id}_data.xlsx`);
  }
  
  // Function to export specific columns from a table to PDF
  function exportTableToPDF(table, columnsToExport) {
    const { jsPDF } = window.jspdf;
    const doc = new jsPDF();
    let rows = table.querySelectorAll("tr");
  
    // Extract headers based on specified columns
    let headers = columnsToExport.map(index => rows[0].cells[index].textContent.trim());
  
    // Extract data rows
    let data = Array.from(rows).slice(1).map(row => {
      return columnsToExport.map(index => row.cells[index].textContent.trim());
    });
  
    doc.text("Table Report", 14, 15);
    doc.autoTable({
      head: [headers],
      body: data,
      startY: 20,
      theme: 'striped',
      headStyles: { fillColor: [58, 128, 186] },
      styles: { fontSize: 10, cellPadding: 2 }
    });
  
    doc.save(`${table.id}_data.pdf`);
  }
  