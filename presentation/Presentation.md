## Code navigation

1. Implemented a simple API to reconcile a product's stock level (step1)

2. Customers complain! (step2)
    - The system is registering inventory lines with wrong product ids
    - The system is not detecting unknown product skus
    - The system is not detecting invalid stock levels (negative values)
    - The system is not detecting invalid reconciliation dates (future dates)
    - The system is not detecting invalid reconciliation dates (dates earlier or equal than the
      last reconciliation date)