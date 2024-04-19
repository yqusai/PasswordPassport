<?php

session_start();

require '../connector.php';

if ($_SERVER["REQUEST_METHOD"] == "POST") {
    $user = $_POST['username'];
    $pwd = $_POST['password'];

    function encryptData($data, $key, $iv) {
        $encrypted = openssl_encrypt($data, 'aes-256-cbc', $key, 0, $iv);
        return base64_encode($iv . $encrypted);
    }

    try {
        // Query to check if username exists and fetch password and encryption key
        $stmt = $conn->prepare("SELECT password, encryption_key FROM users_created WHERE username = ?");
        $stmt->bindParam(1, $user);
        $stmt->execute();
        $result = $stmt->fetch(PDO::FETCH_ASSOC);

        if ($result) {
            // Extract the IV from the stored password for comparison
            $storedData = base64_decode($result['password']);
            $storedIV = substr($storedData, 0, 16);
            $encryptedInputPassword = encryptData($pwd, $result['encryption_key'], $storedIV);

            // Compare the input encrypted password with the stored password
            if ($encryptedInputPassword === $result['password']) {
                $_SESSION["username"] = $user;
                echo '<script>alert("Log in successful!"); window.location.href = "../index.html";</script>';
            } else {
                echo '<script>alert("Invalid username or password"); window.location.href = "signin.html";</script>';
            }
        } else {
            echo '<script>alert("Invalid username or password"); window.location.href = "signin.html";</script>';
        }
    } catch (PDOException $e) {
        die("Query failed: " . $e->getMessage());
    }
} else {
    header("Location: ../index.html");
    exit();
}
?>
