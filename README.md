#### Optimistic and Pessimistic Locking

---

There are two modes of locking available besides Transaction Isolation level:

1. Optimistic Locking
1. Pessimistic Locking

There are different lock modes that can be obtained for these different type of locking. Check test cases implemented here to know more about the locking.

* Optimistic Locking : Using versioning, preferred to handle scenario when system can handle failure of concurrent write and read are more often compared to write 
  * OPTIMISTIC
  * OPTIMISTIC_FORCE_INCREMENT
* Pessimistic Locking : Dependent on database for locking, preferred to handle when write are more
  * PESSIMISTIC_READ            - shared lock
  * PESSIMISTIC_WRITE           - exclusive lock
  * PESSIMISTIC_FORCE_INCREMENT - exclusive lock

Prerequisite
1. Mysql Schema to be created with name - opt_pess_lock
  
_Note_
1. Using Spring @Transactional instead of javax.persistence.transactional
1. Lock time out configuration is not working  
1. Locking is not working with in-memory DB
  
References
1. https://www.baeldung.com/java-jpa-transaction-locks
2. https://www.byteslounge.com/tutorials/locking-in-jpa-lockmodetype  
  
  
  