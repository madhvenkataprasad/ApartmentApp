document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('loginForm');
    const roleSelect = document.getElementById('role');
    const secretCodeGroup = document.getElementById('secretCodeGroup');
    const treasurerKeyGroup = document.getElementById('treasurerKeyGroup');
    const messageDiv = document.getElementById('message');

    // Show/hide additional fields based on role selection
    roleSelect.addEventListener('change', function() {
        if (this.value === 'RESIDENT') {
            secretCodeGroup.style.display = 'block';
            treasurerKeyGroup.style.display = 'none';
            document.getElementById('secretCode').required = true;
            document.getElementById('treasurerKey').required = false;
        } else if (this.value === 'TREASURER') {
            secretCodeGroup.style.display = 'none';
            treasurerKeyGroup.style.display = 'block';
            document.getElementById('secretCode').required = false;
            document.getElementById('treasurerKey').required = true;
        } else {
            secretCodeGroup.style.display = 'none';
            treasurerKeyGroup.style.display = 'none';
            document.getElementById('secretCode').required = false;
            document.getElementById('treasurerKey').required = false;
        }
    });

    loginForm.addEventListener('submit', async function(e) {
        e.preventDefault();

        const formData = {
            username: document.getElementById('username').value,
            password: document.getElementById('password').value
        };

        const role = roleSelect.value;

        // Add role-specific fields
        if (role === 'RESIDENT') {
            formData.secretCode = document.getElementById('secretCode').value;
        } else if (role === 'TREASURER') {
            formData.treasurerKey = document.getElementById('treasurerKey').value;
        }

        try {
            const response = await fetch('/api/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(formData)
            });

            const result = await response.json();

            console.log('Login response:', result);

            if (response.ok) {
                // Store user info in session storage
                sessionStorage.setItem('username', result.username);
                sessionStorage.setItem('email', result.email);
                sessionStorage.setItem('role', result.role);
                sessionStorage.setItem('userId', result.userId);

                console.log('Stored role:', result.role);

                showMessage('Login successful! Redirecting...', 'success');

                // Redirect based on role (handle both formats: "RESIDENT" and "ROLE_RESIDENT")
                const cleanRole = result.role.replace('ROLE_', '');
                console.log('Clean role:', cleanRole);

                setTimeout(() => {
                    if (cleanRole === 'RESIDENT') {
                        console.log('Redirecting to dashboard.html');
                        window.location.href = 'dashboard.html';
                    } else if (cleanRole === 'WATCHMAN') {
                        console.log('Redirecting to watchman-dashboard.html');
                        window.location.href = 'watchman-dashboard.html';
                    } else if (cleanRole === 'TREASURER') {
                        console.log('Redirecting to treasurer-dashboard.html');
                        window.location.href = 'treasurer-dashboard.html';
                    } else {
                        console.log('Unknown role, redirecting to dashboard.html');
                        window.location.href = 'dashboard.html';
                    }
                }, 1000);
            } else {
                console.log('Login failed:', result);
                showMessage(result.error || 'Login failed', 'error');
            }
        } catch (error) {
            showMessage('An error occurred. Please try again.', 'error');
            console.error('Login error:', error);
        }
    });

    function showMessage(text, type) {
        messageDiv.textContent = text;
        messageDiv.className = 'message ' + type;
        setTimeout(() => {
            messageDiv.className = 'message';
        }, 5000);
    }
});
