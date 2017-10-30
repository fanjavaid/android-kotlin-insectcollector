package androidkotlin.fanjavaid.com.insectcollector.data

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by fanjavaid on 10/27/17.
 */
data class Insect(
        var id: Int = 0,
        var name: String = "",
        var info: String = "",
        var picture: String = "",
        var createdAt: String = "",
        var updatedAt: String?) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(info)
        parcel.writeString(picture)
        parcel.writeString(createdAt)
        parcel.writeString(updatedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Insect> {
        override fun createFromParcel(parcel: Parcel): Insect {
            return Insect(parcel)
        }

        override fun newArray(size: Int): Array<Insect?> {
            return arrayOfNulls(size)
        }
    }
}