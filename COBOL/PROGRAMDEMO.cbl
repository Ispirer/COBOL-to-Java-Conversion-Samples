                     IDENTIFICATION DIVISION.
                     PROGRAM-ID. PROGRAMDEMO.

                     DATA DIVISION.
                     WORKING-STORAGE SECTION.

                         EXEC SQL
                            BEGIN DECLARE SECTION
                         END-EXEC.

                           01 USERNAME PIC X(10) VARYING.
                           01 PASSWD   PIC X(10) VARYING.
                           01 DBNAME   PIC X(10) VARYING.

                         EXEC SQL END
                            DECLARE SECTION
                         END-EXEC.

                         EXEC SQL INCLUDE SQLCA END-EXEC.

                        01 ws-1 pic x(1000).

                     PROCEDURE DIVISION.
                     DISPLAY 'Migration ProCOBOL to Oracle PL/SQL'.

                     MOVE "ora" TO USERNAME-ARR.
                     MOVE 4 TO USERNAME-LEN.
                     MOVE "ora" TO PASSWD-ARR.
                     MOVE 3 TO PASSWD-LEN.
                     MOVE "UTEST" TO DBNAME-ARR.
                     MOVE 7 TO DBNAME-LEN.

                     DISPLAY 'CONNECTING...'
                         EXEC SQL
                            CONNECT       :USERNAME
                            IDENTIFIED BY :PASSWD
                            USING         :DBNAME
                         END-EXEC.

                     IF SQLCODE = 0 THEN
                        DISPLAY ' '
                        DISPLAY 'CONNECTION SUCCESSFUL.'
                        DISPLAY 'RUN PROCCESS.'
                        CALL "CRTAB"
                        CALL "MNPROCESS"
                        CALL "DRPTAB" USING 'PROD_PRICE_HIST'
                        DISPLAY 'PROCESS IS FINISHED.'
                     ELSE
                        DISPLAY 'UNABLE TO CONNECT!'
                        DISPLAY 'SQLCODE = ' SQLCODE
                        DISPLAY 'PROGRAM WAS STOPPED!'
                     END-IF.

                     STOP RUN.
