document.addEventListener('DOMContentLoaded', function() {
    const registerForm = document.getElementById('registerForm');
    const roleSelect = document.getElementById('role');
    const treasurerKeyGroup = document.getElementById('treasurerKeyGroup');
    const messageDiv = document.getElementById('message');

    // Show/hide treasurer key field based on role selection
    roleSelect.addEventListener('change', function() {
        if (this.value === 'TREASURER') {
            treasurerKeyGroup.style.display = 'block';
            document.getElementById('treasurerKey').required = true;
        } else {
            treasurerKeyGroup.style.display = 'none';
            document.getElementById('treasurerKey').required = false;
        }
    });

    registerForm.addEventListener('submit', async function(e) {
        e.preventDefault();

        const formData = {
            username: document.getElementById('username').value,
            email: document.getElementById('email').value,
            password: document.getElementById('password').value,
            flatNumber: document.getElementById('flatNumber').value,
            secretCode: document.getElementById('secretCode').value
        };

        // Validate password match
        const confirmPassword = document.getElementById('confirmPassword').value;
        if (formData.password !== confirmPassword) {
            showMessage('Passwords do not match', 'error');
            return;
        }

        const role = roleSelect.value;
        let url = '/api/auth/register';
        let data = formData;

        if (role === 'TREASURER') {
            url = '/api/auth/register/treasurer';
            const treasurerKey = document.getElementById('treasurerKey').value;
            url += '?treasurerKey=' + encodeURIComponent(treasurerKey);
        }

        try {
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            });

            const result = await response.json();

            if (response.ok) {
                showMessage('Registration successful! Please login to continue.', 'success');
                registerForm.reset();
                // Don't auto-redirect, let user click to login
                setTimeout(() => {
                    window.location.href = 'login.html';
                }, 3000);
            } else {
                showMessage(result.error || 'Registration failed', 'error');
            }
        } catch (error) {
            showMessage('An error occurred. Please try again.', 'error');
            console.error('Registration error:', error);
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
