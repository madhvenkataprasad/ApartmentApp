document.addEventListener('DOMContentLoaded', function() {
    // Check if user is logged in - skip role check for now
    const username = document.getElementById('username');
    if (username) {
        username.textContent = 'User';
    }

    // Load dashboard data
    loadDashboardData();
    loadComplaints();
    loadExpenses(); // Preload expenses
    loadAnnouncements(); // Preload announcements
    loadReports(); // Preload reports

    // Navigation
    const navBtns = document.querySelectorAll('.nav-btn');
    const tabContents = document.querySelectorAll('.tab-content');

    navBtns.forEach(btn => {
        btn.addEventListener('click', function() {
            const tab = this.dataset.tab;

            navBtns.forEach(b => b.classList.remove('active'));
            this.classList.add('active');

            tabContents.forEach(content => {
                content.classList.remove('active');
                if (content.id === tab) {
                    content.classList.add('active');
                }
            });
        });
    });

    // Logout
    document.getElementById('logoutBtn').addEventListener('click', async function() {
        try {
            await fetch('/api/auth/logout', {
                method: 'POST'
            });
        } catch (error) {
            console.error('Logout error:', error);
        }

        sessionStorage.clear();
        window.location.href = 'login.html';
    });

    // Filter collections
    const filterCollectionsBtn = document.getElementById('filterCollectionsBtn');
    if (filterCollectionsBtn) {
        filterCollectionsBtn.addEventListener('click', function() {
            const month = parseInt(document.getElementById('collectionMonth').value);
            const year = parseInt(document.getElementById('collectionYear').value);
            console.log('Filtering collections for:', month, year);
            loadCollections(month, year);
        });
    }

    // Filter expenses
    const filterExpensesBtn = document.getElementById('filterExpensesBtn');
    if (filterExpensesBtn) {
        filterExpensesBtn.addEventListener('click', function() {
            const month = parseInt(document.getElementById('expenseMonth').value);
            const year = parseInt(document.getElementById('expenseYear').value);
            console.log('Filtering expenses for:', month, year);
            loadExpenses(month, year);
        });
    }

    // Expense form
    const expenseForm = document.getElementById('expenseForm');
    if (expenseForm) {
        expenseForm.addEventListener('submit', async function(e) {
            e.preventDefault();

            const expenseData = {
                title: document.getElementById('expenseTitle').value,
                description: document.getElementById('expenseDescription').value,
                amount: parseFloat(document.getElementById('expenseAmount').value),
                category: document.getElementById('expenseCategory').value,
                month: parseInt(document.getElementById('expenseMonthInput').value),
                year: parseInt(document.getElementById('expenseYearInput').value),
                billNumber: document.getElementById('billNumber').value,
                vendorName: document.getElementById('vendorName').value
            };

            console.log('Submitting expense:', expenseData);

            try {
                const response = await fetch('/api/treasurer/expenses', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(expenseData)
                });

                const result = await response.json();
                console.log('Expense response:', result);

                if (response.ok) {
                    showMessage('Expense added successfully!', 'success');
                    this.reset();
                    // Immediately reload expenses to show the new one
                    loadExpenses();
                } else {
                    showMessage('Failed to add expense: ' + (result.error || 'Unknown error'), 'error');
                }
            } catch (error) {
                showMessage('An error occurred: ' + error.message, 'error');
                console.error('Expense error:', error);
            }
        });
    } else {
        console.error('Expense form not found');
    }

    // Report form
    const reportForm = document.getElementById('reportForm');
    if (reportForm) {
        reportForm.addEventListener('submit', async function(e) {
            e.preventDefault();

            const month = parseInt(document.getElementById('reportMonth').value);
            const year = parseInt(document.getElementById('reportYear').value);

            console.log('Generating report for:', month, year);

            try {
                const response = await fetch(`/api/treasurer/reports/generate?month=${month}&year=${year}`, {
                    method: 'POST'
                });

                const result = await response.json();
                console.log('Report response:', result);

                if (response.ok) {
                    showMessage('Report generated successfully!', 'success');
                    displayGeneratedReport(result);
                    loadReports();
                } else {
                    showMessage('Failed to generate report: ' + (result.error || 'Unknown error'), 'error');
                }
            } catch (error) {
                showMessage('An error occurred: ' + error.message, 'error');
                console.error('Report error:', error);
            }
        });
    } else {
        console.error('Report form not found');
    }

    // Announcement form
    const announcementForm = document.getElementById('announcementForm');
    if (announcementForm) {
        announcementForm.addEventListener('submit', async function(e) {
            e.preventDefault();

            const announcementData = {
                title: document.getElementById('announcementTitle').value,
                content: document.getElementById('announcementContent').value,
                type: document.getElementById('announcementType').value
            };

            console.log('Submitting announcement:', announcementData);

            try {
                const response = await fetch('/api/treasurer/announcements', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(announcementData)
                });

                const result = await response.json();
                console.log('Announcement response:', result);

                if (response.ok) {
                    showMessage('Announcement published successfully!', 'success');
                    this.reset();
                    loadAnnouncements();
                } else {
                    showMessage('Failed to publish announcement: ' + (result.error || 'Unknown error'), 'error');
                }
            } catch (error) {
                showMessage('An error occurred: ' + error.message, 'error');
                console.error('Announcement error:', error);
            }
        });
    } else {
        console.error('Announcement form not found');
    }

    async function loadDashboardData() {
        try {
            const response = await fetch('/api/treasurer/dashboard');
            const data = await response.json();

            if (response.ok) {
                displayCollections(data.payments);
                displayExpensesList(data.expenses);
                displayReportsList(data.reports);
                displayAnnouncementsList(data.announcements);
            }
        } catch (error) {
            console.error('Error loading dashboard:', error);
        }
    }

    async function loadCollections(month, year) {
        try {
            let url = '/api/treasurer/payments';
            if (month && year) {
                url += `/month?month=${month}&year=${year}`;
            }

            const response = await fetch(url);
            const payments = await response.json();

            if (response.ok) {
                displayCollections(payments);
            }
        } catch (error) {
            console.error('Error loading collections:', error);
        }
    }

    async function loadExpenses(month, year) {
        try {
            let url = '/api/treasurer/expenses';
            if (month && year) {
                url += `/month?month=${month}&year=${year}`;
            }

            const response = await fetch(url);
            const expenses = await response.json();

            if (response.ok) {
                displayExpensesList(expenses);
            }
        } catch (error) {
            console.error('Error loading expenses:', error);
        }
    }

    async function loadReports() {
        try {
            const response = await fetch('/api/treasurer/reports');
            const reports = await response.json();

            if (response.ok) {
                displayReportsList(reports);
            }
        } catch (error) {
            console.error('Error loading reports:', error);
        }
    }

    async function loadAnnouncements() {
        try {
            const response = await fetch('/api/treasurer/announcements');
            const announcements = await response.json();

            if (response.ok) {
                displayAnnouncementsList(announcements);
            }
        } catch (error) {
            console.error('Error loading announcements:', error);
        }
    }

    function displayCollections(payments) {
        const container = document.getElementById('collectionsList');
        container.innerHTML = '';

        if (payments.length === 0) {
            container.innerHTML = '<p class="no-data">No payments yet</p>';
            return;
        }

        payments.forEach(payment => {
            const card = document.createElement('div');
            card.className = 'payment-card';
            card.innerHTML = `
                <div class="payment-header">
                    <h3 class="payment-title">${payment.user?.username || 'Unknown'} - Flat ${payment.user?.flatNumber || 'N/A'}</h3>
                    <span class="payment-date">${payment.month}/${payment.year}</span>
                </div>
                <p class="payment-content">Amount: ₹${payment.amount}</p>
                <p class="payment-content">Method: ${payment.paymentMethod}</p>
                <span class="payment-status ${payment.status.toLowerCase()}">${payment.status}</span>
            `;
            container.appendChild(card);
        });
    }

    function displayExpensesList(expenses) {
        const container = document.getElementById('expensesList');
        container.innerHTML = '';

        if (expenses.length === 0) {
            container.innerHTML = '<p class="no-data">No expenses recorded yet</p>';
            return;
        }

        expenses.forEach(expense => {
            const card = document.createElement('div');
            card.className = 'expense-card';
            card.innerHTML = `
                <div class="expense-header">
                    <h3 class="expense-title">${expense.title}</h3>
                    <span class="expense-date">${new Date(expense.createdAt).toLocaleDateString()}</span>
                </div>
                <p class="expense-content">${expense.description}</p>
                <p class="expense-content">Amount: ₹${expense.amount}</p>
                <span class="expense-category">${expense.category}</span>
                <div class="expense-actions">
                    <button class="btn btn-secondary btn-sm" onclick="deleteExpense(${expense.id})">Delete</button>
                </div>
            `;
            container.appendChild(card);
        });
    }

    function displayReportsList(reports) {
        const container = document.getElementById('reportsList');
        container.innerHTML = '';

        if (reports.length === 0) {
            container.innerHTML = '<p class="no-data">No reports generated yet</p>';
            return;
        }

        reports.forEach(report => {
            const card = document.createElement('div');
            card.className = 'report-card';
            card.innerHTML = `
                <div class="report-header">
                    <h3 class="report-title">${report.month}/${report.year}</h3>
                    <span class="report-date">${new Date(report.createdAt).toLocaleDateString()}</span>
                </div>
                <p class="report-content">Opening Balance: ₹${report.openingBalance}</p>
                <p class="report-content">Total Income: ₹${report.totalIncome}</p>
                <p class="report-content">Total Expenses: ₹${report.totalExpenses}</p>
                <p class="report-content"><strong>Closing Balance: ₹${report.closingBalance}</strong></p>
                <p class="report-content">Residents Paid: ${report.residentsPaid}/${report.totalResidents}</p>
                <span class="report-status">${report.published ? 'Published' : 'Draft'}</span>
                ${!report.published ? `<button class="btn btn-primary btn-sm" onclick="publishReport(${report.id})">Publish</button>` : ''}
            `;
            container.appendChild(card);
        });
    }

    function displayAnnouncementsList(announcements) {
        const container = document.getElementById('announcementsList');
        container.innerHTML = '';

        if (announcements.length === 0) {
            container.innerHTML = '<p class="no-data">No announcements yet</p>';
            return;
        }

        announcements.forEach(announcement => {
            const card = document.createElement('div');
            card.className = 'announcement-card';
            card.innerHTML = `
                <div class="announcement-header">
                    <h3 class="announcement-title">${announcement.title}</h3>
                    <span class="announcement-date">${new Date(announcement.createdAt).toLocaleDateString()}</span>
                </div>
                <p class="announcement-content">${announcement.content}</p>
                <span class="announcement-type">${announcement.type}</span>
                <div class="announcement-actions">
                    <button class="btn btn-secondary btn-sm" onclick="editAnnouncement(${announcement.id})">Edit</button>
                    <button class="btn btn-secondary btn-sm" onclick="deleteAnnouncement(${announcement.id})">Delete</button>
                </div>
            `;
            container.appendChild(card);
        });
    }

    function displayGeneratedReport(report) {
        const container = document.getElementById('generatedReport');
        container.innerHTML = `
            <h3>Report Preview - ${report.month}/${report.year}</h3>
            <div class="report-summary">
                <div class="report-item">
                    <label>Opening Balance</label>
                    <value>₹${report.openingBalance}</value>
                </div>
                <div class="report-item">
                    <label>Total Income</label>
                    <value>₹${report.totalIncome}</value>
                </div>
                <div class="report-item">
                    <label>Total Expenses</label>
                    <value>₹${report.totalExpenses}</value>
                </div>
                <div class="report-item">
                    <label>Closing Balance</label>
                    <value>₹${report.closingBalance}</value>
                </div>
                <div class="report-item">
                    <label>Total Residents</label>
                    <value>${report.totalResidents}</value>
                </div>
                <div class="report-item">
                    <label>Residents Paid</label>
                    <value>${report.residentsPaid}</value>
                </div>
                <div class="report-item">
                    <label>Residents Pending</label>
                    <value>${report.residentsPending}</value>
                </div>
            </div>
            <div class="report-actions">
                <button class="btn btn-primary" onclick="publishReport(${report.id})">Publish Report</button>
            </div>
        `;
    }

    window.deleteExpense = async function(id) {
        if (!confirm('Are you sure you want to delete this expense?')) {
            return;
        }

        try {
            const response = await fetch(`/api/treasurer/expenses/${id}`, {
                method: 'DELETE'
            });

            if (response.ok) {
                showMessage('Expense deleted successfully', 'success');
                loadExpenses();
            } else {
                showMessage('Failed to delete expense', 'error');
            }
        } catch (error) {
            showMessage('An error occurred', 'error');
            console.error('Delete error:', error);
        }
    };

    window.publishReport = async function(id) {
        if (!confirm('Are you sure you want to publish this report? This will notify all residents.')) {
            return;
        }

        try {
            const response = await fetch(`/api/treasurer/reports/${id}/publish`, {
                method: 'POST'
            });

            if (response.ok) {
                showMessage('Report published successfully!', 'success');
                loadReports();
                document.getElementById('generatedReport').innerHTML = '';
            } else {
                showMessage('Failed to publish report', 'error');
            }
        } catch (error) {
            showMessage('An error occurred', 'error');
            console.error('Publish error:', error);
        }
    };

    window.editAnnouncement = async function(id) {
        try {
            const response = await fetch(`/api/treasurer/announcements/${id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    title: prompt('Enter new title:'),
                    content: prompt('Enter new content:'),
                    type: 'GENERAL'
                })
            });

            if (response.ok) {
                showMessage('Announcement updated successfully', 'success');
                loadAnnouncements();
            } else {
                showMessage('Failed to update announcement', 'error');
            }
        } catch (error) {
            showMessage('An error occurred', 'error');
            console.error('Edit error:', error);
        }
    };

    window.deleteAnnouncement = async function(id) {
        if (!confirm('Are you sure you want to delete this announcement?')) {
            return;
        }

        try {
            const response = await fetch(`/api/treasurer/announcements/${id}`, {
                method: 'DELETE'
            });

            if (response.ok) {
                showMessage('Announcement deleted successfully', 'success');
                loadAnnouncements();
            } else {
                showMessage('Failed to delete announcement', 'error');
            }
        } catch (error) {
            showMessage('An error occurred', 'error');
            console.error('Delete error:', error);
        }
    };

    async function loadComplaints() {
        try {
            const response = await fetch('/api/treasurer/complaints');
            const complaints = await response.json();

            if (response.ok) {
                displayComplaints(complaints);
            }
        } catch (error) {
            console.error('Error loading complaints:', error);
        }
    }

    function displayComplaints(complaints) {
        const container = document.getElementById('complaintsList');
        if (!container) return;
        
        container.innerHTML = '';

        if (complaints.length === 0) {
            container.innerHTML = '<p class="no-data">No complaints yet</p>';
            return;
        }

        complaints.forEach(complaint => {
            const card = document.createElement('div');
            card.className = 'complaint-card';
            
            const statusClass = complaint.status.toLowerCase();
            
            card.innerHTML = `
                <div class="complaint-header">
                    <h3 class="complaint-title">${complaint.title}</h3>
                    <span class="complaint-status status-${statusClass}">${complaint.status}</span>
                </div>
                <p class="complaint-category">Category: ${complaint.category}</p>
                <p class="complaint-description">${complaint.description}</p>
                <p class="complaint-date">Submitted: ${new Date(complaint.createdAt).toLocaleDateString()}</p>
                ${complaint.status === 'PENDING' || complaint.status === 'IN_PROGRESS' ? `
                    <div class="complaint-actions">
                        <button onclick="updateComplaintStatus(${complaint.id}, 'IN_PROGRESS')" class="btn btn-small">Mark In Progress</button>
                        <button onclick="resolveComplaint(${complaint.id})" class="btn btn-small btn-primary">Resolve</button>
                    </div>
                ` : ''}
                ${complaint.resolution ? `<p class="complaint-resolution"><strong>Resolution:</strong> ${complaint.resolution}</p>` : ''}
            `;
            container.appendChild(card);
        });
    }

    window.updateComplaintStatus = async function(id, status) {
        try {
            const response = await fetch(`/api/treasurer/complaints/${id}/status?status=${status}`, {
                method: 'PUT'
            });

            if (response.ok) {
                showMessage('Complaint status updated', 'success');
                loadComplaints();
            } else {
                showMessage('Failed to update status', 'error');
            }
        } catch (error) {
            showMessage('An error occurred', 'error');
            console.error('Error:', error);
        }
    };

    window.resolveComplaint = async function(id) {
        const resolution = prompt('Enter resolution details:');
        if (!resolution) return;

        try {
            const response = await fetch(`/api/treasurer/complaints/${id}/resolve?resolution=${encodeURIComponent(resolution)}`, {
                method: 'PUT'
            });

            if (response.ok) {
                showMessage('Complaint resolved successfully', 'success');
                loadComplaints();
            } else {
                showMessage('Failed to resolve complaint', 'error');
            }
        } catch (error) {
            showMessage('An error occurred', 'error');
            console.error('Error:', error);
        }
    };

    function showMessage(text, type) {
        const messageDiv = document.getElementById('message');
        messageDiv.textContent = text;
        messageDiv.className = 'message ' + type;
        setTimeout(() => {
            messageDiv.className = 'message';
        }, 5000);
    }
});
