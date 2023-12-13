       IDENTIFICATION DIVISION.
       PROGRAM-ID. DRPTAB.

       DATA DIVISION.

       WORKING-STORAGE SECTION.

               EXEC SQL
                  INCLUDE SQLCA
               END-EXEC.

       LINKAGE SECTION.
          01 TABLE-NM PIC X(100).

       PROCEDURE DIVISION USING TABLE-NM.
          DISPLAY ' '
          EVALUATE TABLE-NM
             WHEN 'PROD_PRICE_HIST'
                GO TO DROP-PROD-PRICE-HIST
             WHEN OTHER
                DISPLAY 'Unknown action.'
          END-EVALUATE.
          EXIT PROGRAM.

       DROP-PROD-PRICE-HIST.
           EXEC SQL
              DROP TABLE PROD_PRICE_HIST
           END-EXEC.

           IF SQLCODE = 0 THEN
              DISPLAY 'TABLE WAS DROPPED.'
           ELSE
              DISPLAY 'Unable to drop table'
              DISPLAY 'SQLCODE = ' SQLCODE
           END-IF.



