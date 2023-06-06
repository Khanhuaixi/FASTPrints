package com.example.fastprints

class UploadedFile {
    private var name: String = "Unknown Filename"
    private var url: String = "Unknown Url"
    private var orderDate: String = "Unknown order date"
    private var collectDate: String = "Not Collected"
    private var orderId: String = "Unknown Id"
    private var email: String = "Unknown email"
    private var totalPayment: String = "Unknown total payment"
    private var paymentMethod: String = "Unknown payment method"
    private var colourMode: String = "Unknown colour mode"
    private var paperSize: String = "Unknown paper size"
    private var totalCopies: String = "Unknown total copies"

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    constructor() {}
    constructor(name: String, url: String, orderDate: String, collectDate: String, orderId: String
    , email: String, totalPayment: String, paymentMethod: String, colourMode: String, paperSize: String, totalCopies: String) {
        this.name = name
        this.url = url
        this.orderDate = orderDate
        this.collectDate = collectDate
        this.orderId = orderId
        this.email = email
        this.totalPayment = totalPayment
        this.paymentMethod = paymentMethod
        this.colourMode = colourMode
        this.paperSize = paperSize
        this.totalCopies = totalCopies
    }

    fun getName(): String {
        return name
    }
    fun getUrl(): String {
        return url
    }
    fun getOrderDate(): String {
        return orderDate
    }
    fun getCollectDate(): String {
        return collectDate
    }
    fun getOrderId(): String {
        return orderId
    }
    fun getEmail(): String {
        return email
    }
    fun getTotalPayment(): String {
        return totalPayment
    }
    fun getPaymentMethod(): String {
        return paymentMethod
    }
    fun getColourMode(): String {
        return colourMode
    }
    fun getPaperSize(): String {
        return paperSize
    }
    fun getTotalCopies(): String {
        return totalCopies
    }
}
