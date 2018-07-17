package magetower

import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField

fun serialize(thing : Any) : String {

    val s = StringBuilder()
    serializeClass(thing,s)

    return ""
}

fun serializeClass(thing : Any, s : StringBuilder) {
    println("serializing $thing")
    try {
        val props = thing::class.memberProperties
        props.forEach { prop ->
            prop.isAccessible = true
            if(prop.javaField != null && prop.javaField!!.isEnumConstant) {
                println("${prop.name} is an enum with value ${prop.getter.call(thing)}")
            }
            if(prop.javaField?.type == String::class.java) {
                println("${prop.name} is a String with value ${prop.getter.call(thing)}")
            }
            else if(prop.javaField?.type == Int::class.java) {
                println("${prop.name} is a int with value ${prop.getter.call(thing)}")
            }
            else if(prop.javaField?.type == ArrayList::class.java) {
                var list = prop.getter.call(thing) as ArrayList<*>
                serializeList(list, s)
            }
            else if(prop.javaField?.type == List::class.java) {
                var list = prop.getter.call(thing) as List<*>
                serializeList(list, s)
            }
            else if(prop.javaField?.type == HashMap::class.java) {
                var map = prop.getter.call(thing) as HashMap<*,*>
                serializeMap(map, s)
            }
            else {
                serializeClass(prop.getter.call(thing)!!, s)
            }
        }
    } catch (e : Exception ) {
        //e.printStackTrace()
    }

}

fun serializeList(list : List<*>, s : StringBuilder) {
    println("serializing list:  $list")
    list.forEach { elem ->
        if(elem!!::class == String::class) {
            println("String with value $elem")
        }
        else if(elem::class == Int::class) {
            println("Int with value $elem")
        }
        else if(elem::class == ArrayList::class) {
            var subList = elem as ArrayList<*>
            serializeList(subList,s)
        }
        else if(elem::class == List::class) {
            var subList = elem as ArrayList<*>
            serializeList(subList,s)
        }
        else if(elem::class == HashMap::class.java) {
            var map = elem as HashMap<*,*>
            serializeMap(map, s)
        }
        else {
            serializeClass(elem, s)
        }
    }
}

fun serializeMap(map : HashMap<*,*>, s : StringBuilder) {
    map.forEach { elem ->
        println(elem.key)
        if(elem.value::class == String::class) {
            println("String with value ${elem.value}")
        }
        else if(elem.value::class == Int::class) {
            println("Int with value ${elem.value}")
        }
        else if(elem.value::class == ArrayList::class) {
            var subList = elem.value as ArrayList<*>
            serializeList(subList,s)
        }
        else if(elem.value::class == HashMap::class.java) {
            var subMap = elem.value as HashMap<*,*>
            serializeMap(subMap, s)
        }
        else {
            serializeClass(elem.value, s)
        }
    }
}