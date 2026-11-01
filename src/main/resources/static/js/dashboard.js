document.addEventListener('DOMContentLoaded', function() {
    // Check if user is logged in - skip role check for now
    const username = document.getElementById('username');
    if (username) {
        username.textContent = 'User';
    }
    document.getElementById('userRole').textContent = 'Resident Dashboard';

    // Load dashboard data
    loadDashboardData();

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

    // Payment modal
    const paymentModal = document.getElementById('paymentModal');
    const makePaymentBtn = document.getElementById('makePaymentBtn');
    const closeBtn = document.querySelector('.close-btn');

    if (makePaymentBtn) {
        makePaymentBtn.addEventListener('click', function() {
            paymentModal.classList.add('active');
        });
    }

    if (closeBtn) {
        closeBtn.addEventListener('click', function() {
            paymentModal.classList.remove('active');
        });
    }

    if (paymentModal) {
        paymentModal.addEventListener('click', function(e) {
            if (e.target === paymentModal) {
                paymentModal.classList.remove('active');
            }
        });
    }

    // Complaint modal
    const complaintModal = document.getElementById('complaintModal');
    const addComplaintBtn = document.getElementById('addComplaintBtn');
    const closeBtns = document.querySelectorAll('.close-btn');

    if (addComplaintBtn) {
        addComplaintBtn.addEventListener('click', function() {
            complaintModal.classList.add('active');
        });
    }

    closeBtns.forEach(btn => {
        btn.addEventListener('click', function() {
            paymentModal.classList.remove('active');
            complaintModal.classList.remove('active');
        });
    });

    if (complaintModal) {
        complaintModal.addEventListener('click', function(e) {
            if (e.target === complaintModal) {
                complaintModal.classList.remove('active');
            }
        });
    }

    // Complaint form
    const complaintForm = document.getElementById('complaintForm');
    if (complaintForm) {
        complaintForm.addEventListener('submit', async function(e) {
            e.preventDefault();

            const complaintData = {
                title: document.getElementById('complaintTitle').value,
                description: document.getElementById('complaintDescription').value,
                category: document.getElementById('complaintCategory').value
            };

            console.log('Submitting complaint:', complaintData);

            try {
                const response = await fetch('/api/resident/complaints', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(complaintData)
                });

                const result = await response.json();
                console.log('Complaint response:', result);

                if (response.ok) {
                    showMessage('Complaint submitted successfully!', 'success');
                    complaintForm.reset();
                    complaintModal.classList.remove('active');
                    loadComplaints();
                } else {
                    showMessage('Failed to submit complaint: ' + (result.error || 'Unknown error'), 'error');
                }
            } catch (error) {
                showMessage('An error occurred: ' + error.message, 'error');
                console.error('Complaint error:', error);
            }
        });
    } else {
        console.error('Complaint form not found');
    }

    // Payment form
    const paymentForm = document.getElementById('paymentForm');
    if (paymentForm) {
        paymentForm.addEventListener('submit', async function(e) {
            e.preventDefault();

            const paymentData = {
                amount: parseFloat(document.getElementById('paymentAmount').value),
                month: parseInt(document.getElementById('paymentMonth').value),
                year: parseInt(document.getElementById('paymentYear').value),
                paymentMethod: document.getElementById('paymentMethod').value,
                notes: document.getElementById('paymentNotes').value
            };

            console.log('Submitting payment:', paymentData);

            try {
                const response = await fetch('/api/resident/payments', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(paymentData)
                });

                const result = await response.json();
                console.log('Payment response:', result);

                if (response.ok) {
                    // For demo purposes, immediately mark as paid
                    const completeResponse = await fetch(`/api/resident/payments/${result.id}/complete?transactionId=DEMO_${Date.now()}`, {
                        method: 'POST'
                    });

                    if (completeResponse.ok) {
                        showMessage('Payment recorded successfully!', 'success');
                        paymentForm.reset();
                        paymentModal.classList.remove('active');
                        loadPayments();
                    } else {
                        showMessage('Payment initiated but could not complete', 'error');
                    }
                } else {
                    showMessage('Payment failed: ' + (result.error || 'Unknown error'), 'error');
                }
            } catch (error) {
                showMessage('An error occurred: ' + error.message, 'error');
                console.error('Payment error:', error);
            }
        });
    } else {
        console.error('Payment form not found');
    }

    async function loadDashboardData() {
        try {
            const response = await fetch('/api/resident/dashboard');
            const data = await response.json();

            if (response.ok) {
                displayAnnouncements(data.announcements);
                displayPayments(data.payments);
                displayExpenses(data.expenses);
                displayReports(data.reports);
                displayNotifications(data.notifications);

                // Update notification badge
                const badge = document.getElementById('notificationBadge');
                if (data.unreadCount > 0) {
                    badge.textContent = data.unreadCount;
                } else {
                    badge.style.display = 'none';
                }
            }
        } catch (error) {
            console.error('Error loading dashboard:', error);
        }

        // Load complaints separately
        loadComplaints();
        
        // Also load expenses and reports directly to ensure they appear
        loadExpensesDirect();
        loadReportsDirect();
    }

    async function loadExpensesDirect() {
        try {
            const response = await fetch('/api/resident/expenses');
            const expenses = await response.json();
            if (response.ok) {
                displayExpenses(expenses);
            }
        } catch (error) {
            console.error('Error loading expenses:', error);
        }
    }

    async function loadReportsDirect() {
        try {
            const response = await fetch('/api/resident/reports');
            const reports = await response.json();
            if (response.ok) {
                displayReports(reports);
            }
        } catch (error) {
            console.error('Error loading reports:', error);
        }
    }

    async function loadComplaints() {
        try {
            const response = await fetch('/api/resident/complaints');
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
                ${complaint.resolution ? `<p class="complaint-resolution"><strong>Resolution:</strong> ${complaint.resolution}</p>` : ''}
            `;
            container.appendChild(card);
        });
    }

    function displayAnnouncements(announcements) {
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
            `;
            container.appendChild(card);
        });
    }

    function displayPayments(payments) {
        const container = document.getElementById('paymentsList');
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
                    <h3 class="payment-title">₹${payment.amount}</h3>
                    <span class="payment-date">${payment.month}/${payment.year}</span>
                </div>
                <p class="payment-content">Method: ${payment.paymentMethod}</p>
                <span class="payment-status ${payment.status.toLowerCase()}">${payment.status}</span>
            `;
            container.appendChild(card);
        });
    }

    function displayExpenses(expenses) {
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
            `;
            container.appendChild(card);
        });
    }

    function displayReports(reports) {
        const container = document.getElementById('reportsList');
        container.innerHTML = '';

        if (reports.length === 0) {
            container.innerHTML = '<p class="no-data">No reports published yet</p>';
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
                <span class="report-status">Published</span>
            `;
            container.appendChild(card);
        });
    }

    function displayNotifications(notifications) {
        const container = document.getElementById('notificationsList');
        container.innerHTML = '';

        if (notifications.length === 0) {
            container.innerHTML = '<p class="no-data">No notifications</p>';
            return;
        }

        notifications.forEach(notification => {
            const card = document.createElement('div');
            card.className = 'notification-card' + (notification.read ? '' : ' unread');
            card.innerHTML = `
                <div class="notification-header">
                    <h3 class="notification-title">${notification.title}</h3>
                    <span class="notification-date">${new Date(notification.createdAt).toLocaleDateString()}</span>
                </div>
                <p class="notification-content">${notification.message}</p>
                <span class="notification-type">${notification.type}</span>
            `;
            container.appendChild(card);
        });
    }

    async function loadPayments() {
        try {
            const response = await fetch('/api/resident/payments');
            const payments = await response.json();
            displayPayments(payments);
        } catch (error) {
            console.error('Error loading payments:', error);
        }
    }

    function handleUPIPayment(paymentId, amount) {
        fetch(`/api/payment/upi/qr-code?paymentId=${paymentId}&amount=${amount}`)
            .then(response => response.json())
            .then(data => {
                window.open(data.qrCodeUrl, '_blank');
                showMessage('Scan the QR code to pay via UPI', 'info');
            })
            .catch(error => {
                console.error('Error generating QR code:', error);
            });
    }

    function handleRazorpayPayment(paymentId, amount) {
        fetch(`/api/payment/razorpay/create-order?paymentId=${paymentId}`)
            .then(response => response.json())
            .then(data => {
                // In production, integrate Razorpay checkout
                showMessage('Razorpay integration would open here', 'info');
            })
            .catch(error => {
                console.error('Error creating Razorpay order:', error);
            });
    }

    function showMessage(text, type) {
        const messageDiv = document.getElementById('message');
        messageDiv.textContent = text;
        messageDiv.className = 'message ' + type;
        setTimeout(() => {
            messageDiv.className = 'message';
        }, 5000);
    }
});
