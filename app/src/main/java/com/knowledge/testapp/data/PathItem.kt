package com.knowledge.testapp.data

import android.os.Parcel
import android.os.Parcelable

class PathItem : Parcelable {
    var _id: Int = 0
    var titleStart: String? = null
    var titleGoal: String? = null
    var path: String? = null
    var pathLength: Int = 0
    var success: Int = -1


    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(this._id)
        dest.writeString(this.titleStart)
        dest.writeString(this.titleGoal)
        dest.writeString(this.path)
        dest.writeInt(this.pathLength)
        dest.writeInt(this.success)
    }

    constructor() {}

    protected constructor(`in`: Parcel) {
        this._id = `in`.readInt()
        this.titleStart = `in`.readString()
        this.titleGoal = `in`.readString()
        this.path = `in`.readString()
        this.pathLength = `in`.readInt()
        this.success = `in`.readInt()
    }

    companion object {

        @JvmField val CREATOR: Parcelable.Creator<PathItem> = object : Parcelable.Creator<PathItem> {
            override fun createFromParcel(source: Parcel): PathItem {
                return PathItem(source)
            }

            override fun newArray(size: Int): Array<PathItem?> {
                return arrayOfNulls(size)
            }
        }
    }
}