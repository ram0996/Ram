package com.affirm.payment.service.executor.result;

import de.hybris.platform.payment.model.PaymentTransactionEntryModel;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AffirmPaymentServiceResult {private static final String TRANSACTION_ENTRY = "transactionEntry";

   private final Map<String, Object> data = new HashMap<>();

   public static AffirmPaymentServiceResult create()
   {
      return new AffirmPaymentServiceResult();
   }

   public static AffirmPaymentServiceResult create(PaymentTransactionEntryModel transactionEntryModel)
   {
      AffirmPaymentServiceResult affirmPaymentServiceResult = new AffirmPaymentServiceResult();
      affirmPaymentServiceResult.addTransaction(transactionEntryModel);
      return  affirmPaymentServiceResult;
   }

   public AffirmPaymentServiceResult addData(final String name, final Object value)
   {
      data.put(name, value);
      return this;
   }

   /**
    * Returns payment result as map
    *
    * @return a map whith data
    */
   public Map<String, Object> getData()
   {
      return Collections.unmodifiableMap(data);
   }

   /**
    * Returns payment result data for specified name.
    *
    * @return a payment result data object
    */
   @SuppressWarnings("unchecked")
   public <T> T getData(final String name)
   {
      return (T) data.get(name);
   }

   /**
    * Sets payment transaction entry on the result data.
    *
    * @param transaction the transaction entry to be set
    * @return this
    */
   public AffirmPaymentServiceResult addTransaction(final PaymentTransactionEntryModel transaction)
   {
      data.put(TRANSACTION_ENTRY, transaction);
      return this;
   }

   /**
    * Returns payment transaction from result data.
    *
    * @return the resulting payment transaction entry
    */
   public PaymentTransactionEntryModel getTransaction()
   {
      return (PaymentTransactionEntryModel) data.get(TRANSACTION_ENTRY);
   }

   @Override
   public String toString()
   {
      return "PaymentServiceResult { " + data.keySet() + " }";
   }
}
