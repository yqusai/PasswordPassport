<?php
// Start the session
session_start();
require '../connector.php';

// Check if a session exists
if (!isset($_SESSION["username"])) {
    // Redirect to the login page if the user is not logged in
    header("Location: ../sign_folder/signin.html");
    exit();
}


function decryptData($encryptedData, $key) {
    $data = base64_decode($encryptedData);
    $iv = substr($data, 0, 16);
    $encryptedText = substr($data, 16);

    $decryptedData = openssl_decrypt($encryptedText, 'aes-256-cbc', $key, 0, $iv);

    if ($decryptedData === false) {
        // Handle decryption error
        echo 'Decryption error: ' . openssl_error_string();
    }

    return $decryptedData;
}

// Fetch passwords for the logged-in user
$loggedInUser = $_SESSION["username"];
$tableName = $loggedInUser . "_passwords";
$checkTableQuery = "SHOW TABLES LIKE '$tableName'";
$tableExists = $conn->query($checkTableQuery)->rowCount() > 0;


?>


<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons+Sharp" rel="stylesheet">
    <title>Using Password Manager</title>
    <link rel="stylesheet" href="dashboard.css">
    <script src="generatescript.js" defer></script>


</head>
<body>
    <div class="container">
        <aside>
            <div class="top">
                <div class="logo">
                    <img src="../images/logo.png" alt="logo">
                    <h2> <span class="danger"></span> </h2>
                </div>
                <div class="close" id="close-btn">
                    <span class="material-icons-sharp">close</span>
                </div>
            </div>
            <div class="sidebar">
                <a class="active" href="#" id="dashboard-link">
                    <span class="material-icons-sharp"> grid_view</span>
                    <h3>Dashboard</h3>
                </a>
                <a href="#" id="websites-link">
                    <span class="material-icons-sharp"> web</span>
                    <h3>Websites</h3>
                </a>
                <a href="#" id="applications-link">
                    <span class="material-icons-sharp">apps</span>
                    <h3>Applications</h3>
                </a>
                <a href="#" id="recently-link">
                    <span class="material-icons-sharp">schedule</span>
                    <h3>Recently Changed</h3>
                </a>
                <a href="#" id="generator-link">
                    <span class="material-icons-sharp">design_services </span>
                    <h3>Password Generator</h3>
                </a>
                <a href="#" id="add-link">
                    <span class="material-icons-sharp">add</span>
                    <h3>Add Password</h3>
                </a>
                <a href="../signout.php">
                    <span class="material-icons-sharp">logout</span>
                    <h3>Sign Out</h3>
                </a>
            </div>
        </aside>
        <main>
        <main>
            <?php
            if ($tableExists) {
                // Display Dashboard section if the table exists
                ?>
                <section id="dashboard-page" class="active">
                    <table>
                        <tr>
                            <th>Website Name</th>
                            <th>Timestamp</th>
                            <th>Password</th>
                        </tr>
                        <?php
                        $sql = "SELECT website_name, password, timestamp, encryption_key FROM $tableName";
                        $result = $conn->query($sql);

                        if ($result->rowCount() > 0) {
                            while ($row = $result->fetch()) {
                                $decryptedPassword = decryptData($row['password'], $row['encryption_key']);
                                echo "<tr>
                                    <td>", htmlspecialchars($row["website_name"]), "</td>
                                    <td>", $row["timestamp"], "</td>
                                    <td><button class='show-hide' onclick='togglePasswordVisibility(this)'>Show</button><span class='password' style='display:none;'>", htmlspecialchars($decryptedPassword), "</span></td>
                                </tr>";
                            }
                        } else {
                            echo "No passwords found.";
                        }
                        ?>
                    </table>
                </section>
            <?php
            }
            ?>

            <?php
            if ($tableExists) {
                // Display Dashboard section if the table exists
                ?>
                <section id="websites-page">
                    <table>
                        <tr>
                            <th>Website Name</th>
                            <th>Timestamp</th>
                            <th>Password</th>
                        </tr>
                        <?php
                        $sql = "SELECT website_name, password, timestamp, encryption_key FROM $tableName WHERE password_type = 'website'";
                        $result = $conn->query($sql);

                        if ($result->rowCount() > 0) {
                            while ($row = $result->fetch()) {
                                $decryptedPassword = decryptData($row['password'], $row['encryption_key']);
                                echo "<tr>
                                    <td>", htmlspecialchars($row["website_name"]), "</td>
                                    <td>", $row["timestamp"], "</td>
                                    <td><button class='show-hide' onclick='togglePasswordVisibility(this)'>Show</button><span class='password' style='display:none;'>", htmlspecialchars($decryptedPassword), "</span></td>
                                </tr>";
                            }
                        } else {
                            echo "No passwords found.";
                        }
                        ?>
                    </table>
                </section>
            <?php
            }
            ?>

<?php
            if ($tableExists) {
                // Display Dashboard section if the table exists
                ?>
                <section id="applications-page">
                    <table>
                        <tr>
                            <th>Website Name</th>
                            <th>Timestamp</th>
                            <th>Password</th>
                        </tr>
                        <?php
                        $sql = "SELECT website_name, password, timestamp, encryption_key FROM $tableName WHERE password_type = 'application'";
                        $result = $conn->query($sql);

                        if ($result->rowCount() > 0) {
                            while ($row = $result->fetch()) {
                                $decryptedPassword = decryptData($row['password'], $row['encryption_key']);
                                echo "<tr>
                                    <td>", htmlspecialchars($row["website_name"]), "</td>
                                    <td>", $row["timestamp"], "</td>
                                    <td><button class='show-hide' onclick='togglePasswordVisibility(this)'>Show</button><span class='password' style='display:none;'>", htmlspecialchars($decryptedPassword), "</span></td>
                                </tr>";
                            }
                        } else {
                            echo "No passwords found.";
                        }
                        ?>
                    </table>
                </section>
            <?php
            }
            ?>
<?php
            if ($tableExists) {
                // Display Dashboard section if the table exists
                ?>
                <section id="recently-page">
                    <table>
                        <tr>
                            <th>Website Name</th>
                            <th>Timestamp</th>
                            <th>Password</th>
                        </tr>
                        <?php
                        $sql = "SELECT website_name, password, timestamp, encryption_key FROM $tableName ORDER BY timestamp ASC";
                        $result = $conn->query($sql);

                        if ($result->rowCount() > 0) {
                            while ($row = $result->fetch()) {
                                $decryptedPassword = decryptData($row['password'], $row['encryption_key']);
                                echo "<tr>
                                    <td>", htmlspecialchars($row["website_name"]), "</td>
                                    <td>", $row["timestamp"], "</td>
                                    <td><button class='show-hide' onclick='togglePasswordVisibility(this)'>Show</button><span class='password' style='display:none;'>", htmlspecialchars($decryptedPassword), "</span></td>
                                </tr>";
                            }
                        } else {
                            echo "No passwords found.";
                        }
                        ?>
                    </table>
                </section>
            <?php
            }
            ?>
            
            <section id="generator-page">

                <div class="pw-container">
                    <div class="pw-header">
                        <div class="pw">
                            <span id="pw">l823Zs78#Css09@</span>
        
                        </div>
                    </div>
                    <div class="pw-body">
                        <div class="form-control">
                            <label for="len">Password Length</label>
                            <input id="len" value="15" type="number" min="2" max="30" />
                        </div>
                        <div class="form-control">
                            <label for="upper">Contain Uppercase Letters</label>
                            <input id="upper" type="checkbox" />
                        </div>
                        <div class="form-control">
                            <label for="lower">Contain Lowercase Letters</label>
                            <input id="lower" type="checkbox" />
                        </div>
                        <div class="form-control">
                            <label for="number">Contain Numbers</label>
                            <input id="number" type="checkbox" />
                        </div>
                        <div class="form-control">
                            <label for="symbol">Contain Symbols</label>
                            <input id="symbol" type="checkbox" />
                        </div>
                        <button class="generate" id="generate">
                            Generate Password
                        </button>
                    </div>
                </div>
            </section>


            <section id="add-password-page">
                <div class="add-container">
                <form id="add-password-form" action="add-password.php" method="post" onsubmit="addPassword(event)">
                    <div class="form-control-add">
                        <label for="website-name">Website Name</label>
                        <input name="website-name" id="website-name" type="text" required />
                    </div>
                    <div class="form-control-add">
                        <label for="password">Password</label>
                        <input name="password" id="password" type="password" required />
                        <div class="password-strength-container">
                            <span>Strength:</span>
                            <div id="password-strength-meter"></div>
                        </div>
                        <p id="password-strength-text"></p>
                    </div>
                    <div class="form-control-add">
                        <label for="password-type">Password Type</label>
                        <select name="password-type" id="password-type" required>
                            <option value="website">Website</option>
                            <option value="application">Application</option>
                            <option value="other">Other</option>
                        </select>
                    </div>
                    <button type="submit">Save Password</button>
                </form>
                </div>
            </section>

        </main>

    </div>

    <script>
        document.getElementById('dashboard-link').addEventListener('click', function() {
            showPage('dashboard-page');
            setActiveButton('dashboard-link');
        });
        

        document.getElementById('websites-link').addEventListener('click', function() {
            showPage('websites-page');
            setActiveButton('websites-link');
        });

        document.getElementById('applications-link').addEventListener('click', function() {
            showPage('applications-page');
            setActiveButton('applications-link');
        });
        document.getElementById('recently-link').addEventListener('click', function() {
            showPage('recently-page');
            setActiveButton('recently-link');
        });
        document.getElementById('generator-link').addEventListener('click', function() {
            showPage('generator-page');
            setActiveButton('generator-link');
        });

        document.getElementById('add-link').addEventListener('click', function() {
            showPage('add-password-page');
            setActiveButton('add-link');
        });

    document.getElementById('password').addEventListener('input', function() {
        var password = this.value;
        var strengthBar = document.getElementById('password-strength-meter');
        var strengthText = document.getElementById('password-strength-text');
        var strength = 0;

        if (password.match(/[a-zA-Z0-9][a-zA-Z0-9]+/)) {
            strength += 1;
        }
        if (password.match(/[~<>?]+/)) {
            strength += 1;
        }
        if (password.match(/[!@#$%^&*()]+/)) {
            strength += 1;
        }
        if (password.length > 5) {
            strength += 1;
        }
        if (password.length > 10) {
            strength += 2;
        }

        console.log('Password:', password); // Logs the password input
        console.log('Strength score:', strength); // Logs the calculated strength

        switch(strength) {
            case 0:
            case 1:
                strengthBar.className = "strength-weak";
                strengthText.textContent = "Weak";
                break;
            case 2:
            case 3:
                strengthBar.className = "strength-medium";
                strengthText.textContent = "Medium";
                break;
            case 4:
            case 5:
                strengthBar.className = "strength-strong";
                strengthText.textContent = "Strong";
                break;
        }

        console.log('Current class:', strengthBar.className); // Logs the class added to the meter

        strengthBar.style.width = (strength / 5 * 90) + '%';
});


        document.addEventListener('DOMContentLoaded', function () {
            var revealButtons = document.querySelectorAll('.reveal-button');

            revealButtons.forEach(function (button) {
                button.addEventListener('click', function () {
                    var passwordSpan = button.nextElementSibling; // Assuming the password is the next sibling element
                    passwordSpan.style.display = passwordSpan.style.display === 'none' ? 'inline' : 'none';
                });
            });
        });

        function showPage(pageId) {
            var pages = document.querySelectorAll('main section');
            pages.forEach(function(page) {
                page.classList.remove('active');
            });

            document.getElementById(pageId).classList.add('active');
        }

        function setActiveButton(buttonId) {
            var buttons = document.querySelectorAll('.sidebar a');
            buttons.forEach(function(button) {
                button.classList.remove('active');
            });

            document.getElementById(buttonId).classList.add('active');
        }
        function togglePasswordVisibility(button) {
        var passwordSpan = button.nextElementSibling;
        if (passwordSpan.style.display === 'none') {
            passwordSpan.style.display = 'inline';
            button.textContent = 'Hide';
        } else {
            passwordSpan.style.display = 'none';
            button.textContent = 'Show';
        }
    }
    </script>
</body>
</html>
