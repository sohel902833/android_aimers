package com.example.aimers.Model;

public class NewBatchPaymentModel {

    PaymentBatch paymentBatch;
    Double totalPaid;

    public  NewBatchPaymentModel(){}
    public NewBatchPaymentModel(PaymentBatch paymentBatch, Double totalPaid) {
        this.paymentBatch = paymentBatch;
        this.totalPaid = totalPaid;
    }

    public PaymentBatch getPaymentBatch() {
        return paymentBatch;
    }

    public void setPaymentBatch(PaymentBatch paymentBatch) {
        this.paymentBatch = paymentBatch;
    }

    public Double getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(Double totalPaid) {
        this.totalPaid = totalPaid;
    }
}
