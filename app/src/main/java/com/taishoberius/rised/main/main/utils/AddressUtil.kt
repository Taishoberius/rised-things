package com.taishoberius.rised.main.main.utils

class AddressUtil {
    companion object {
        fun getAddress(address: String?): String? {
            if (address == null) return null
            val split = address.split('|')
            if (split.isEmpty()) return null;

            return split[0]
        }

        fun getCity(address: String?): String? {
            if (address == null) return null
            val split = address.split('|')
            if (split.isEmpty()) return null;
            if (split.count() > 1)
                return split[1]

            return null
        }

        fun getZipCode(address: String?): String? {
            if (address == null) return null
            val split = address.split('|')
            if (split.isEmpty()) return null;
            if (split.count() > 2)
                return split[2]

            return null
        }
    }
}