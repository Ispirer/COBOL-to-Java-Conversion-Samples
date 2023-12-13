         05 FILLER                  PIC X(1) value '|'.
         05 (*)-GRPNM                   PIC X(7).
         05 FILLER                  PIC X(1) value '|'.
         05 (*)-GRP-REC-NM              PIC 9(5) VALUE 0.
         05 FILLER                  PIC X(1) value '|'.
         05 (*)-AV-PRICES.
            10 (*)-WS-PREV-DAY-P-AV     PIC 9(10)v9(6) VALUE 0.
            10 FILLER                  PIC X(1) value '|'.
            10 (*)-WS-LATEST-P-AV       PIC 9(10)v9(4) VALUE 0.
            10 FILLER                  PIC X(1) value '|'.
            10 (*)-WS-END-OF-MNTH-P-AV  PIC 9(10)v9(8) VALUE 0.
            10 FILLER                  PIC X(1) value '|'.
