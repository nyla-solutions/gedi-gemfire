BEGIN
DBMS_NETWORK_ACL_ADMIN.CREATE_ACL(
acl => 'http_service.xml',
description => 'HTTP ACL',
principal => 'GEMDB',
is_grant => true,
privilege => 'connect');
END;



BEGIN
DBMS_NETWORK_ACL_ADMIN.ADD_PRIVILEGE(
acl => 'http_service.xml',
principal => 'GEMDB',
is_grant => true,
privilege => 'resolve');
END;

BEGIN
DBMS_NETWORK_ACL_ADMIN.ASSIGN_ACL(                       
  acl => 'http_service.xml',                               
  host => '192.168.0.11');  
 END; 
 
 COMMIT;

 