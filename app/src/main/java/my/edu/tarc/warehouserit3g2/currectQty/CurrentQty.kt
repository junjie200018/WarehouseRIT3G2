package my.edu.tarc.warehouserit3g2.currectQty

data class CurrentQty(
    var PartNo: String,
    var Qty: String,
    var progress: Double,
    var BelowMin : String
)
