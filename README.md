# Password manager

This project has two tools for implementing password managment:
  -usermgmt (for admin to manage passwords and usernames)
  -login (for users to log in)
Both are used in the command line and will be explained bellow.

Usermgmt:
  - adding a new user (start the program and add two arguments: add and the name of the user):
    - $ ./usermgmt add user1 
    - Password: 
    - Repeat Password: 
    - User add successfuly added. (This will be displayed differently if its a password missmatch, not strong enough password or user exists)
