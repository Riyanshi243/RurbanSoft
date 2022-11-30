<?php
 
/*
* Database Constants
* Make sure you are putting the values according to your database here 
*/
//xampp connect php code
define('DB_HOST','127.0.0.1');
define('DB_USERNAME','root');
define('DB_PASSWORD','');
define('DB_NAME', 'mrurban');
 
//Connecting to the database
$conn = new mysqli(DB_HOST, DB_USERNAME, DB_PASSWORD, DB_NAME);
 
//checking the successful connection
if($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}
 
//making an array to store the response
$response = array(); 
 
//if there is a post request move ahead s
if($_SERVER['REQUEST_METHOD']=='POST'){
 
 //getting the name from request 
 $UserName = $_POST['UserName']; 
 $UserPhoneNumber=$_POST['UserPhoneNumber'];
 $State=$_POST['State'];
 $District=$_POST['District'];
 $Cluster=$_POST['Cluster'];
 $GP=$_POST['GP'];
 $Components=$_POST['Components'];
 $SubComponents=$_POST['SubComponents'];
 $Status=$_POST['Status'];
 $Phase=$_POST['Phase'];
 $Latitude=$_POST['Latitude'];
 $Longitude=$_POST['Longitude'];
 $Image= base64_decode($_POST['Image']);
 $DateTime=$_POST['DateTime'];


 $lat=doubleval($Latitude);
 $lon=doubleval($Longitude);
 
 //creating a statement to insert to database 
 $stmt = $conn->prepare("INSERT INTO workitem (UserName, UserPhoneNumber, State, District, Cluster, GP,Components, SubComponents, Status, Phase, Latitude,Longitude, Image, DateTime) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
 
 //binding the parameter to statement 
 $stmt->bind_param("ssssssssssddss", $UserName, $UserPhoneNumber, $State, $District, $Cluster,$GP, $Components, $SubComponents, $Status, $Phase, $lat, $lon, $Image, $DateTime );
 
 //if data inserts successfully
 if($stmt->execute()){
 //making success response 
 $response['error'] = false; 
 $response['message'] = 'Saved successfully'; 
 }else{
 //if not making failure response 
 $response['error'] = true; 
 $response['message'] = 'Please try later';
 }
 
}else{
 $response['error'] = true; 
 $response['message'] = "Invalid request"; 
}
 
//displaying the data in json format 
echo json_encode($response);

?>