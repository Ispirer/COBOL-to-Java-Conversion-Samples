       IDENTIFICATION DIVISION.
       PROGRAM-ID. CRTAB.

       DATA DIVISION.
       WORKING-STORAGE SECTION.

           EXEC SQL
              BEGIN DECLARE SECTION
           END-EXEC.

             01 var1 PIC 9(10).
             01 var2 PIC X(10).
             01 var3 PIC X(10).

           EXEC SQL END
              DECLARE SECTION
           END-EXEC.

           EXEC SQL INCLUDE SQLCA END-EXEC.

       PROCEDURE DIVISION.
       DISPLAY ' '.
       DISPLAY 'CREATING PROD_PRICE_HIST TABLE AND INSERTING DATA...'

           EXEC SQL
               CREATE TABLE PROD_PRICE_HIST
               (
                   PROD_SYMB        VARCHAR2(13),
                   PREV_DAY_PRICE   NUMBER(8,3),
                   LATEST_PRICE   NUMBER(8,3),
                   END_OF_MNTH_PRICE   NUMBER(8,3)
               )
           END-EXEC.

           IF SQLCODE = 0 THEN
              DISPLAY 'Table was created.'
              PERFORM INSERT-DATA
              EXIT PROGRAM
           ELSE
              DISPLAY 'UNABLE TO CREATE TABLE!'
              DISPLAY 'SQLCODE = ' SQLCODE
              DISPLAY 'PROGRAM WAS STOPPED!'
              STOP RUN
           END-IF.

       INSERT-DATA.
           EXEC SQL
              INSERT INTO PROD_PRICE_HIST
              SELECT PROD.PROD_NM || PROD.PROD_GR,
                     A.PREV_DAY_PRICE, B.LATEST_PRICE,
                     C.END_OF_MNTH_PRICE
              FROM PROD, PROD_PREV_DAY_PR A, PROD_LATEST_PR B,
                   PROD_END_OF_MNTH_PR C
              WHERE PROD.PROD_ID = A.PROD_ID AND
                    B.PROD_ID = C.PROD_ID AND
                    PROD.PROD_ID = B.PROD_ID
           END-EXEC.

           IF SQLCODE = 0 THEN
              EXEC SQL
                 COMMIT
              END-EXEC
              DISPLAY 'DATA WAS INSERTED.'
           ELSE
              DISPLAY 'UNABLE TO INSERT DATA!'
              DISPLAY 'SQLCODE = ' SQLCODE
              CALL "DRPTAB" USING 'PROD_PRICE_HIST'
              DISPLAY 'PROGRAM WAS STOPPED'
              STOP RUN
           END-IF.




