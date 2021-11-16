# ZeBank

# INFO

MySql is used for database

# Changes required in database

1. create a table named zebank with columns 
    account_id varchar(12) primarykey
    user_name varchar(45)
    balance double
    
2. create a table named zebank_trans with columns
    trans_id int(3) primarykey auto increment
    account_id varchar(12) foreignkey
    debit double 
    credit double
    
# Changes required in code

In ZeBankService class change the database name, username, and password according to your database at class instances.
        
# Run 

Run the ZeBankService file.
