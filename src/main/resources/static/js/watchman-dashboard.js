document.addEventListener('DOMContentLoaded', function() {
    // Check if user is logged in - skip role check for now
    const username = document.getElementById('username');
    if (username) {
        username.textContent = 'User';
    }

    // Load announcements
    loadAnnouncements();
    loadComplaints();
    // Preload all data on startup

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
                const response = await fetch('/api/watchman/announcements', {
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

    async function loadAnnouncements() {
        try {
            const response = await fetch('/api/watchman/announcements');
            const announcements = await response.json();

            if (response.ok) {
                displayAnnouncements(announcements);
            }
        } catch (error) {
            console.error('Error loading announcements:', error);
        }
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
                <div class="announcement-actions">
                    <button class="btn btn-secondary btn-sm" onclick="editAnnouncement(${announcement.id})">Edit</button>
                    <button class="btn btn-secondary btn-sm" onclick="deleteAnnouncement(${announcement.id})">Delete</button>
                </div>
            `;
            container.appendChild(card);
        });
    }

    window.editAnnouncement = async function(id) {
        try {
            const response = await fetch(`/api/watchman/announcements/${id}`, {
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
            const response = await fetch(`/api/watchman/announcements/${id}`, {
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
            const response = await fetch('/api/watchman/complaints');
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
            const response = await fetch(`/api/watchman/complaints/${id}/status?status=${status}`, {
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
            const response = await fetch(`/api/watchman/complaints/${id}/resolve?resolution=${encodeURIComponent(resolution)}`, {
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
