package my.edu.tarc.warehouserit3g2.stockInOut

data class Stock(
    var PartNo: String,
    var RecBy : String,
    var RecDate: String,
    var qty   : String,
    var SerialNo : String,
    var Status : String,
    var RackId : String,
    var RackInDate : String,
    var RackOutDate : String
)