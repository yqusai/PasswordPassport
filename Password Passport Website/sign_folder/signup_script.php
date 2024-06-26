<?php
if ($_SERVER["REQUEST_METHOD"] == "POST") {
    require "../connector.php";


    function encryptData($data, $key, $iv) {
        $encrypted = openssl_encrypt($data, 'aes-256-cbc', $key, 0, $iv);
        return base64_encode($iv . $encrypted);
    }

    $user = $_POST["username"];
    $email = $_POST["email"];
    $pwd = $_POST["password"];
    $encryptionKey = random_bytes(32);
    $iv = random_bytes(16); 
    $encryptedPassword = encryptData($pwd, $encryptionKey, $iv);

    try {
        $checkTableQuery = "SHOW TABLES LIKE 'users_created'";
        $tableStmt = $conn->query($checkTableQuery);
        if ($tableStmt->rowCount() == 0) {
            $createTableQuery = "CREATE TABLE users_created (
                email VARCHAR(255),
                username VARCHAR(255),
                password VARCHAR(255),
                encryption_key BINARY(32)
            )";
            $conn->exec($createTableQuery);
        }

        $checkUserQuery = "SELECT * FROM users_created WHERE username = ? OR email = ?";
        $checkUserStmt = $conn->prepare($checkUserQuery);
        $checkUserStmt->execute([$user, $email]);
        if ($checkUserStmt->rowCount() > 0) {
            echo '<script>alert("Username or email already in use!"); window.location.href = "signup.html";</script>';
        }

        $insertQuery = "INSERT INTO users_created (email, username, password, encryption_key) VALUES (?, ?, ?, ?);";
        $stmt = $conn->prepare($insertQuery);
        $stmt->execute([$email, $user, $encryptedPassword, $encryptionKey]);

        $conn = null;
        header("Location: ../index.html");
        die();
    } catch (PDOException $e) {
        die("Query failed: " . $e->getMessage());
    }
} else {
    header("Location: ../index.html");
}
?>

