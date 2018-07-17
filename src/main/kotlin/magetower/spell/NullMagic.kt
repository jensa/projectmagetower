package magetower.spell

import kotlinx.serialization.Serializable

@Serializable
class NullMagic : MagicBranch("Null", "null", HashMap()) {
}