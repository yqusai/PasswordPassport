<?php
session_start();
require '../connector.php';

function encryptData($data, $key, $iv) {
    $encrypted = openssl_encrypt($data, 'aes-256-cbc', $key, 0, $iv);
    return base64_encode($iv . $encrypted);
}

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $websiteName = $_POST['website-name'];
    $password = $_POST['password'];
    $passwordType = $_POST['password-type'];

    $encryptionKey = random_bytes(32); // 256 bits key
    $iv = random_bytes(16); 

    $encryptedPassword = encryptData($password, $encryptionKey, $iv);

    $tableName = $_SESSION["username"] . "_passwords";
    $checkTableQuery = "SHOW TABLES LIKE '$tableName'";
    $tableExists = $conn->query($checkTableQuery)->rowCount() > 0;

    if (!$tableExists) {
        $createTableQuery = "CREATE TABLE $tableName (
            id INT AUTO_INCREMENT PRIMARY KEY,
            website_name VARCHAR(255) NOT NULL,
            password VARCHAR(255) NOT NULL,
            password_type VARCHAR(50) NOT NULL,
            timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            encryption_key BINARY(32)
        )";
        $conn->exec($createTableQuery);
    }

    $insertQuery = "INSERT INTO $tableName (website_name, password, password_type, encryption_key) VALUES (?, ?, ?, ?)";
    $stmt = $conn->prepare($insertQuery);
    $stmt->execute([$websiteName, $encryptedPassword, $passwordType, $encryptionKey]);


    echo '<script>alert("Password added successfully!"); window.location.href = "dashboard.php";</script>';
} else {
    header("Location: ../index.html");
    exit();
}
?>
