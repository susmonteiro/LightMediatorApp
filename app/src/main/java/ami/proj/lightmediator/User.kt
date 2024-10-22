package ami.proj.lightmediator

import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable

data class User(val name: String?, val id: Int, val color: Int) : Parcelable {
    private var spokenTime = 0.0

    constructor(source: Parcel) : this(
        source.readString(),
        source.readInt(),
        source.readInt()
    )

    @JvmName("getId1")
    fun getId(): Int { return id }

    fun getSpokenTime(): Double { return spokenTime }

    fun addSpokenTime(time: Double) { spokenTime += time }

    fun getTimeText(): String {
        val minutes = (spokenTime/60).toInt()
        val seconds = ((spokenTime - minutes*60)).toInt()
        return String.format("%dm %02ds", minutes, seconds)
    }

    fun getColor(): List<Int> {
        return listOf(
            Color.red(color),
            Color.green(color),
            Color.blue(color)
        )
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(name)
        writeInt(id)
        writeInt(color)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<User> = object : Parcelable.Creator<User> {
            override fun createFromParcel(source: Parcel): User = User(source)
            override fun newArray(size: Int): Array<User?> = arrayOfNulls(size)
        }
    }
}