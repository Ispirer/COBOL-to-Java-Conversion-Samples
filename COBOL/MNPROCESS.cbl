              IDENTIFICATION DIVISION.
              PROGRAM-ID. MNPROCESS.

              ENVIRONMENT DIVISION.
              INPUT-OUTPUT SECTION.
              FILE-CONTROL.
                   SELECT I-AVALGR-FILE ASSIGN TO 'AVALGR'
                       ORGANIZATION IS LINE SEQUENTIAL
                       ACCESS IS SEQUENTIAL.

                   SELECT O-AVREP-FILE ASSIGN TO 'AVREP'
                       ORGANIZATION IS LINE SEQUENTIAL
                       ACCESS IS SEQUENTIAL.

               DATA DIVISION.
               FILE SECTION.

                   FD I-AVALGR-FILE.
                   01 I-AVALGR-REC               PIC X(3).

                   FD O-AVREP-FILE.
                   COPY "AVREPREC".

              WORKING-STORAGE SECTION.

                   EXEC SQL
                       BEGIN DECLARE SECTION
                   END-EXEC.

                      01 WS-CNT                  PIC 9(7).

                   EXEC SQL END
                       DECLARE SECTION
                   END-EXEC.

                   EXEC SQL INCLUDE

                     PRHIST.cpy

                   END-EXEC.

                   01  LOAD-HIST-STATUS          PIC X(02) VALUE SPACES.
                       88  READ-HIST  VALUE SPACES.
                       88  HIST-EOF   VALUE HIGH-VALUES.

                   01  HISTORY-TABLE.
                       05 WS-HIST-CNT            PIC 9(5).
                       05 PROD-HISTORIES
                            OCCURS 0 TO 1000 TIMES
                            DEPENDING ON WS-HIST-CNT
                            INDEXED BY WS-HIST-INX.
                          10 WS-PROD-PRICE-HISTORY.
                             15 WS-PROD-SYMB     PIC X(13).
                             15 WS-PROD-SYMB-DT  REDEFINES WS-PROD-SYMB.
                                20 WS-PROD-DT    PIC X(10).
                                20 WS-PROD-GR    PIC X(3).
                             15 WS-PREV-DAY-P    PIC 9(5)v9(3).
                             15 WS-LATEST-P      PIC 9(5)v9(3).
                             15 WS-END-OF-MNTH-P PIC 9(5)v9(3).

                   01 PROD-GRP OCCURS 3 TIMES.
                      COPY "AVGRPR" REPLACING ==(*)== BY ==i==.

                  exec sql include sqlca end-exec.

              PROCEDURE DIVISION.

              INITIALIZE PROD-PRICE-HISTORY.

              MAIN-1000.
                 PERFORM INITIALIZE-COUNT.
                 IF WS-CNT > 0 THEN
                    PERFORM LOAD-HISTORY.

              INITIALIZE-COUNT.
                   EXEC SQL
                       SELECT count(*)
                       INTO WS-CNT
                       FROM PROD_PRICE_HIST
                   END-EXEC.
                   DISPLAY 'PROD-HISTORY-REC NUMBER: ' WS-CNT.
                   DISPLAY ' '.

              LOAD-HISTORY.
                 DISPLAY 'LOADING OF PRODUCT HISTORY RECORDS...'.
                 PERFORM DECLARE-HISTORY-CUR-2000 THRU EXIT-2000.
                 SET WS-HIST-INX TO 0.
                 PERFORM FETCH-HISTORY-CUR THRU SET-HISTORY-TAB
                         UNTIL HIST-EOF.

              DECLARE-HISTORY-CUR-2000.
                  EXEC SQL
                      DECLARE C1 CURSOR FOR
                      SELECT PROD_SYMB, PREV_DAY_PRICE,
                      LATEST_PRICE, END_OF_MNTH_PRICE
                      FROM PROD_PRICE_HIST
                  END-EXEC.

              OPEN-HISTORY-CUR-2000.
                  EXEC SQL
                      OPEN C1
                  END-EXEC.
                  IF SQLCODE <> 0 THEN
                     DISPLAY 'ERROR! SQLCODE - ' SQLCODE
                     GO TO CLOSE-HISTORY-CUR.

              EXIT-2000.
                 EXIT.

              FETCH-HISTORY-CUR.
                  EXEC SQL
                      FETCH C1
                      INTO :PROD-SYMB,
                           :PREV-DAY-P,
                           :LATEST-P,
                           :END-OF-MNTH-P
                  END-EXEC.

                  IF SQLCODE = +1403 THEN
                     SET  HIST-EOF TO TRUE
                     GO TO CLOSE-HISTORY-CUR
                  ELSE IF SQLCODE <> 0 THEN
                     DISPLAY 'ERROR! SQLCODE - ' SQLCODE
                     GO TO CLOSE-HISTORY-CUR.

              SET-HISTORY-TAB.
                 DISPLAY PROD-PRICE-HISTORY.
                 SET WS-HIST-INX UP BY 1.
                 MOVE PROD-PRICE-HISTORY TO
                      WS-PROD-PRICE-HISTORY(WS-HIST-INX).

              CLOSE-HISTORY-CUR.
                 DISPLAY 'LOADING FINISHED.'.
                 MOVE WS-CNT TO WS-HIST-CNT.
                 EXEC SQL
                    CLOSE C1
                 END-EXEC.

              AVAL-GRPS-READ.
                 DISPLAY ' '.
                 OPEN INPUT I-AVALGR-FILE.
                 DISPLAY 'AVAILABLE GROUPS: '.
                 PERFORM TEST AFTER VARYING WS-CNT
                 FROM 1 BY 1 UNTIL WS-CNT = 3
                    READ I-AVALGR-FILE INTO I-GRPNM(WS-CNT)
                    AT END
                       CLOSE I-AVALGR-FILE
                    NOT AT END
                       DISPLAY I-GRPNM(WS-CNT).

              PROCESS-AVPRICE.
                 PERFORM HISTORY-FIND-AV-PRICES
                 THRU FIND-AV-PRICES-EXIT.

                 EXIT PROGRAM.

              HISTORY-FIND-AV-PRICES.
                 DISPLAY ' '.
                 DISPLAY 'CALCULATION OF THE AVERAGE COST BY GROUPS...'.
                 PERFORM TEST AFTER VARYING WS-HIST-INX
                    FROM 1 BY 1 UNTIL WS-HIST-INX = WS-HIST-CNT
                    EVALUATE WS-PROD-GR(WS-HIST-INX)
                      WHEN I-GRPNM(1)
                        ADD 1 TO I-GRP-REC-NM(1)
                        COMPUTE I-WS-PREV-DAY-P-AV(1) =
                                I-WS-PREV-DAY-P-AV(1) +
                                WS-PREV-DAY-P(WS-HIST-INX)
                        COMPUTE I-WS-LATEST-P-AV(1) =
                                I-WS-LATEST-P-AV(1) +
                                WS-LATEST-P(WS-HIST-INX)
                        COMPUTE I-WS-END-OF-MNTH-P-AV(1) =
                                I-WS-END-OF-MNTH-P-AV(1) +
                                WS-END-OF-MNTH-P(WS-HIST-INX)
                      WHEN I-GRPNM(2)
                        ADD 1 TO I-GRP-REC-NM(2)
                        COMPUTE I-WS-PREV-DAY-P-AV(2) =
                                I-WS-PREV-DAY-P-AV(2) +
                                WS-PREV-DAY-P(WS-HIST-INX)
                        COMPUTE I-WS-LATEST-P-AV(2) =
                                I-WS-LATEST-P-AV(2) +
                                WS-LATEST-P(WS-HIST-INX)
                        COMPUTE I-WS-END-OF-MNTH-P-AV(2) =
                                I-WS-END-OF-MNTH-P-AV(2) +
                                WS-END-OF-MNTH-P(WS-HIST-INX)
                      WHEN I-GRPNM(3)
                        ADD 1 TO I-GRP-REC-NM(3)
                        COMPUTE I-WS-PREV-DAY-P-AV(3) =
                                I-WS-PREV-DAY-P-AV(3) +
                                WS-PREV-DAY-P(WS-HIST-INX)
                        COMPUTE I-WS-LATEST-P-AV(3) =
                                I-WS-LATEST-P-AV(3) +
                                WS-LATEST-P(WS-HIST-INX)
                        COMPUTE I-WS-END-OF-MNTH-P-AV(3) =
                                I-WS-END-OF-MNTH-P-AV(3) +
                                WS-END-OF-MNTH-P(WS-HIST-INX)
                      WHEN OTHER
                        DISPLAY 'WRONG GROUP WAS FOUND.'
                    END-EVALUATE.

                 DISPLAY ' '.

                 STRING '| GROUP '
                      '| NUM '
                      '| PREV DAY PRICE '
                      '| LATEST PRICE '
                      '| END OF MNTH PRICE|'
                      INTO O-AVREP-REC.

                 DISPLAY O-AVREP-REC.

                 PERFORM TEST AFTER VARYING WS-CNT
                 FROM 1 BY 1 UNTIL WS-CNT = 3
                    COMPUTE I-WS-PREV-DAY-P-AV(WS-CNT) =
                            I-WS-PREV-DAY-P-AV(WS-CNT) / 3
                    COMPUTE I-WS-LATEST-P-AV(WS-CNT) =
                            I-WS-LATEST-P-AV(WS-CNT) / 3
                    COMPUTE I-WS-END-OF-MNTH-P-AV(WS-CNT) =
                            I-WS-END-OF-MNTH-P-AV(WS-CNT) / 3
                    DISPLAY PROD-GRP(WS-CNT).

                 DISPLAY 'CALCULATION IS FINISHED.'.
                 DISPLAY ' '.

              WRITE-REP-HEADER.
                 OPEN OUTPUT O-AVREP-FILE.
                 WRITE O-AVREP-REC.
                 CLOSE O-AVREP-FILE.

              WRITE-AVPRICE-VAL.
                 OPEN EXTEND O-AVREP-FILE.
                 PERFORM TEST AFTER VARYING WS-CNT
                 FROM 1 BY 1 UNTIL WS-CNT = 3
                    WRITE O-AVREP-REC FROM PROD-GRP(WS-CNT).
                 CLOSE O-AVREP-FILE.

              FIND-AV-PRICES-EXIT.
                 EXIT.



