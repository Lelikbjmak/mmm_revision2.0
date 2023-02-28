-- user repository test
insert into roles values
(1, 'ROLE_USER');

insert into users values (1, null, 1, 1, 1, 1, 0, 'test1@gmail.com',
'$argon2id$v=19$m=23552,t=2,p=1$vs6B0GzSxNN59ETxIeu8/Xs$sv7heef2KsSJfVFUpyq/kfndnN7s4p8e3TqQI79CogE',
'testUser'), -- success user
(2, '2030-01-01' , 1, 0, 1, 1, 3, 'test2@gmail.com',
'$argon2id$v=19$m=23552,t=2,p=1$vs6B0GzSxNN59ETxIeu8/Xs$sv7heef2KsSJfVFUpyq/kfndnN7s4p8e3TqQI79CogE',
'lockedUser'), -- locked user
(3, null, 1, 1, 1, 0, 0, 'test3@gmail.com',
'$argon2id$v=19$m=23552,t=2,p=1$vs6B0GzSxNN59ETxIeu8/Xs$sv7heef2KsSJfVFUpyq/kfndnN7s4p8e3TqQI79CogE',
'disabledUser'), -- user disabled
(4, null, 0, 1, 0, 1, 0, 'test4@gmail.com',
'$argon2id$v=19$m=23552,t=2,p=1$vs6B0GzSxNN59ETxIeu8/Xs$sv7heef2KsSJfVFUpyq/kfndnN7s4p8e3TqQI79CogE',
'expiredUser'); -- user expired

insert into users_roles values
(1, 1),
(2, 1),
(3, 1);