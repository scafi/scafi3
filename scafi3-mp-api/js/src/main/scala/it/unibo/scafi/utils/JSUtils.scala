package it.unibo.scafi.utils

import scala.scalajs.js

@SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf"))
object JSUtils:

  extension (jsObject: js.Object)

    /** Convert a `js.Object` to a `js.Dynamic` object for dynamic access. */
    def asDynamic: js.Dynamic = jsObject.asInstanceOf[js.Dynamic]

  extension (value: js.Dynamic)

    /** @return true if the dynamic object has all the specified functions. */
    def hasFunctions(functionNames: String*): Boolean = functionNames.forall: name =>
      js.typeOf(value.selectDynamic(name)) == "function"

    /**
     * @return
     *   true if the dynamic object has all the specified properties with the expected types.
     * @see
     *   [[typed]] for a convenient way to create the property name/type pairs.
     */
    def hasProps(properties: (PropertyName, PropertyType)*): Boolean = properties.forall:
      case (name, expectedType) =>
        !js.isUndefined(value.selectDynamic(name)) && js.typeOf(value.selectDynamic(name)) == expectedType

  /** A type alias for property names used in dynamic checks. */
  type PropertyName = String

  /** A type alias for property types used in dynamic checks. */
  type PropertyType = String

  extension (propertyName: PropertyName)

    /** Associates the property name with its expected type for dynamic checks. */
    inline infix def typed(propertyType: PropertyType): (PropertyName, PropertyType) = (propertyName, propertyType)

end JSUtils
