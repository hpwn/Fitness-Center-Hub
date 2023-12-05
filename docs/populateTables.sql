-- courses in november
INSERT INTO course VALUES (11, 'Strength 101', 1200, TO_DATE('2023-11-02','YYYY-MM-DD'), TO_DATE('2023-11-12', 'YYYY-MM-DD'), 0, 50);
INSERT INTO course VALUES (12, 'Yoga 101', 1000, TO_DATE('2023-11-10','YYYY-MM-DD'), TO_DATE('2023-11-18','YYYY-MM-DD'), 0, 50);
INSERT INTO course VALUES (13, 'Strength 102', 0800, TO_DATE('2023-11-20','YYYY-MM-DD'), TO_DATE('2023-11-25','YYYY-MM-DD'), 0, 50);
INSERT INTO course VALUES (14, 'Yoga 102', 1200, TO_DATE('2023-11-2','YYYY-MM-DD'), TO_DATE('2023-11-12','YYYY-MM-DD'), 0, 50);

--courses not in november
INSERT INTO course VALUES (15, 'Big Boys 100', 1800, TO_DATE('2023-06-17','YYYY-MM-DD'), TO_DATE('2023-10-17','YYYY-MM-DD'), 0, 50);
INSERT INTO course VALUES (16, 'Biggest Boys 100', 2100, TO_DATE('2023-01-01','YYYY-MM-DD'), TO_DATE('2023-02-01','YYYY-MM-DD'), 0, 50);

-- courses in december
INSERT INTO course VALUES (17, 'December Bachelor Club', 600, TO_DATE('2023-12-12','YYYY-MM-DD'), TO_DATE('2023-12-16','YYYY-MM-DD'), 0, 10);


--course packages in november
INSERT INTO coursePackage VALUES (20, 'Junior Package', 49.99, 11, 12);
INSERT INTO coursePackage VALUES (21, 'Senior Package', 99.99, 13, 14);
INSERT INTO coursePackage VALUES (22, 'Strength Package', 199.99, 11, 13);

--course packages not in november
INSERT INTO coursePackage VALUES (23, 'Big boys package', 999.99, 15, 16);

--inserting members with classes in november
INSERT INTO member VALUES (100, 'John', 'Stamos', '123-456-789', 20, 50, 49.99, 0.00);
INSERT INTO member VALUES (101, 'Tilda', 'Swinton', '234-567-891', 21, 50, 99.99, 0.00);
INSERT INTO member VALUES (102, 'Johnny', 'Depp', '456-789-1234', 22, 50, 199.99, 0.00);
INSERT INTO member VALUES (103, 'Kiera', 'Knightly', '567-891-2345', 20, 50, 199.99, 0.00);
INSERT INTO member VALUES (104, 'John', 'Lithgow', '678-912-3456', 23, 50, 999.99, 0.00);

--insert trainers 
INSERT INTO trainer VALUES (1001, 'Brittney', '111-222-3333');
INSERT INTO trainer VALUES (1002, 'Chad', '222-333-4444');
INSERT INTO trainer VALUES (1003, 'Beefy boy', '333-444-5555');

--creating relation between trainer and class
INSERT INTO trainClass VALUES (1001, 12);
INSERT INTO trainClass VALUES (1002, 15);
INSERT INTO trainClass VALUES (1001, 16);
INSERT INTO trainClass VALUES (1003, 13);
INSERT INTO trainClass VALUES (1003, 14);
INSERT INTO trainClass VALUES (1002, 17);

