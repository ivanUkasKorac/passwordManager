# Password manager

This project has two tools for implementing password managment:
  -usermgmt (for admin to manage passwords and usernames)
  -login (for users to log in)
Both are used in the command line and will be explained bellow.

Usermgmt:
  1. adding a new user (start the program and add two arguments: "add" and the name of the user):
     - $ ./usermgmt add user1 
     - Password: 
     - Repeat Password: 
     - User add successfuly added. (This will be displayed differently if its a password missmatch, weak password or user exists)
  2. changing a password of a user that already exists (start the program and add two arguments: "passwd" and the name of the user):
    - $ ./usermgmt passwd user1 
    - Password: 
    - Repeat Password: 
    - Password change successful. (This will be displayed differently if its a password missmatch, weak password or user doesn't exist)
  3. forcing a password change on next user login (start the program and add two arguments: "forcepass" and the name of the user):
    -$ ./usermgmt forcepass user1
    -User will be requested to change password on next login. (This will be displayed differently if user doesn't exist)
  4. deleting an existing user (start the program and add two arguments: "del" and the name of the user):
    -$ ./usermgmt del user1
    -User successfuly removed. (This will be displayed differently if user doesn't exist)
    
Login:
  1. loging in with username and password while the password is hidden (start the program and add one argument: the name of the user):
    -$ ./login user1
    -Password:
    -Login successful. (This will be displayed differently if wrong password or username is entered : "Username or password incorrect.")
  2. password change after successful login - if admin forced it (start the program and add one argument: the name of the user):
    -$ ./login sgros
    -Password:          
    -New password:      (If the first password was incorrect or an incorrect username here it would display: "Username or password incorrect.")
    -Repeat new password:
    -Login successful. (This will be displayed differently if its a password missmatch or an old password is used, weak password or user doesn't exist)
    
Both the username and password can only be 256 characters long, and are only ASCII characters (codes from 33 to 126 inclusive).

Even if an unwanted person got to the file with the passwords it should be safe because of the implementation of the following.

Safety features:
  1. Safe storage:
    -a random value (salt) is added to each password, which is passed through the cryptographic digest function
    -salt and the result of the cryptographic function are stored in the file
    -the function used by the program is PBKDF2WithHmacSHA1, which protects passwords from "brute force", "rainbow tables" attacks and is resistant to collisions
    -the algorithm increases its intensity with iterations, which makes it more secure than some others (e.g. MD5)
    -the program now works based on comparing the results of cryptographic functions, which makes it difficult for attackers to do their job
  2. Preventing guessing:
    -password complexity of at least 10 characters and at least one number and uppercase letter is specified
    -after changing the password, changing to the current (old) password is prohibited
  3. Periodic change:
    -the administrator can request a password change
    -after a certain time, the system itself asks for a password change (currently set to 30 days) which ensures more frequent password changes
  4. Theft:
    -disabled recording of password when logging in or changing password

	
    
    
