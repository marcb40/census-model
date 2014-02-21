CREATE TABLE rate (
  id int(11) NOT NULL,
  product_id in(11) DEFAULT NULL,
  min_age int(11) DEFAULT NULL,
  max_age int(11) DEFAULT NULL,
  rate decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`id`)
)

insert into rate (id, product_id, min_age, max_age, rate) values (1, 1, 0, 21, 88.9);
insert into rate (id, product_id, min_age, max_age, rate) values (2, 1, 22, 55, 95.2);
insert into rate (id, product_id, min_age, max_age, rate) values (3, 1, 56, 99, 101.33);

insert into rate (id, product_id, min_age, max_age, rate) values (4, 2, 0, 21, 77.5);
insert into rate (id, product_id, min_age, max_age, rate) values (6, 2, 22, 55, 78.6);
insert into rate (id, product_id, min_age, max_age, rate) values (7, 2, 56, 99, 99.5);

commit;