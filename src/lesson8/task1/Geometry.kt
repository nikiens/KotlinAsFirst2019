@file:Suppress("UNUSED_PARAMETER")

package lesson8.task1

import lesson1.task1.sqr
import kotlin.math.*

/**
 * Точка на плоскости
 */
data class Point(val x: Double, val y: Double) {
    /**
     * Пример
     *
     * Рассчитать (по известной формуле) расстояние между двумя точками
     */
    fun distance(other: Point): Double = sqrt(sqr(x - other.x) + sqr(y - other.y))
}

/**
 * Треугольник, заданный тремя точками (a, b, c, см. constructor ниже).
 * Эти три точки хранятся в множестве points, их порядок не имеет значения.
 */
@Suppress("MemberVisibilityCanBePrivate")
class Triangle private constructor(private val points: Set<Point>) {

    private val pointList = points.toList()

    val a: Point get() = pointList[0]

    val b: Point get() = pointList[1]

    val c: Point get() = pointList[2]

    constructor(a: Point, b: Point, c: Point) : this(linkedSetOf(a, b, c))

    /**
     * Пример: полупериметр
     */
    fun halfPerimeter() = (a.distance(b) + b.distance(c) + c.distance(a)) / 2.0

    /**
     * Пример: площадь
     */
    fun area(): Double {
        val p = halfPerimeter()
        return sqrt(p * (p - a.distance(b)) * (p - b.distance(c)) * (p - c.distance(a)))
    }

    /**
     * Пример: треугольник содержит точку
     */
    fun contains(p: Point): Boolean {
        val abp = Triangle(a, b, p)
        val bcp = Triangle(b, c, p)
        val cap = Triangle(c, a, p)
        return abp.area() + bcp.area() + cap.area() <= area()
    }

    override fun equals(other: Any?) = other is Triangle && points == other.points

    override fun hashCode() = points.hashCode()

    override fun toString() = "Triangle(a = $a, b = $b, c = $c)"
}

/**
 * Окружность с заданным центром и радиусом
 */
data class Circle(val center: Point, val radius: Double) {
    /**
     * Простая
     *
     * Рассчитать расстояние между двумя окружностями.
     * Расстояние между непересекающимися окружностями рассчитывается как
     * расстояние между их центрами минус сумма их радиусов.
     * Расстояние между пересекающимися окружностями считать равным 0.0.
     */
    fun distance(other: Circle): Double =
        (center.distance(other.center) - (radius + other.radius)).let {
            if (it >= 0) it else 0.0
        }

    /**
     * Тривиальная
     *
     * Вернуть true, если и только если окружность содержит данную точку НА себе или ВНУТРИ себя
     */
    fun contains(p: Point): Boolean =
        sqr(center.x - p.x) + sqr(center.y - p.y) <= sqr(radius)
}

/**
 * Отрезок между двумя точками
 */
data class Segment(val begin: Point, val end: Point) {
    override fun equals(other: Any?) =
        other is Segment && (begin == other.begin && end == other.end || end == other.begin && begin == other.end)

    override fun hashCode() =
        begin.hashCode() + end.hashCode()

    val midpoint = Point((begin.x + end.x) / 2, (begin.y + end.y) / 2)
}

/**
 * Средняя
 *
 * Дано множество точек. Вернуть отрезок, соединяющий две наиболее удалённые из них.
 * Если в множестве менее двух точек, бросить IllegalArgumentException
 */

/**
 * Основные части функций diameter и findNearestCirclePair выполняют схожие роли.
 * Возможно, имеет смысл выделить эту часть в отдельную функцию, если возможно передать
 * minBy или maxBy в аргументы, находящую близжайшие и удаленные окружности, а
 * точки в таком случае считать за окружности с нулевым радиусом
 */
fun diameter(vararg points: Point): Segment {
    require(points.size >= 2)

    val found = points.groupBy({ it }, { (points.toList() - it).toSet() })
        .map { entry ->
            entry.key to entry.value[0].map { point ->
                point to point.distance(entry.key)
            }.maxBy { it.second }
        }.toMap().maxBy { it.value!!.second }!!

    return Segment(found.key, found.value!!.first)
}

/**
 * Простая
 *
 * Построить окружность по её диаметру, заданному двумя точками
 * Центр её должен находиться посередине между точками, а радиус составлять половину расстояния между ними
 */
fun circleByDiameter(diameter: Segment): Circle {
    val o = diameter.midpoint
    val r = diameter.begin.distance(diameter.end) / 2

    return Circle(o, r)
}

/**
 * Прямая, заданная точкой point и углом наклона angle (в радианах) по отношению к оси X.
 * Уравнение прямой: (y - point.y) * cos(angle) = (x - point.x) * sin(angle)
 * или: y * cos(angle) = x * sin(angle) + b, где b = point.y * cos(angle) - point.x * sin(angle).
 * Угол наклона обязан находиться в диапазоне от 0 (включительно) до PI (исключительно).
 */
class Line private constructor(val b: Double, val angle: Double) {
    init {
        require(angle >= 0 && angle < PI) { "Incorrect line angle: $angle" }
    }

    constructor(point: Point, angle: Double) : this(point.y * cos(angle) - point.x * sin(angle), angle)

    /**
     * Средняя
     *
     * Найти точку пересечения с другой линией.
     * Для этого необходимо составить и решить систему из двух уравнений (каждое для своей прямой)
     */
    fun crossPoint(other: Line): Point {
        val b1 = b / cos(angle)
        val b2 = other.b / cos(other.angle)

        val x = (b2 - b1) / (tan(angle) - tan(other.angle))
        val y =
            if (angle != PI / 2) {
                x * tan(angle) + b1
            } else {
                x * tan(other.angle) + b2
            }

        return Point(x, y)
    }

    override fun equals(other: Any?) = other is Line && angle == other.angle && b == other.b

    override fun hashCode(): Int {
        var result = b.hashCode()
        result = 31 * result + angle.hashCode()
        return result
    }

    override fun toString() = "Line(${cos(angle)} * y = ${sin(angle)} * x + $b)"
}

fun removePeriod(rad: Double): Double {
    var angle = rad

    while (angle < 0.0 || angle >= PI)
        if (angle >= PI) angle -= PI else angle += PI

    return angle
}

/**
 * Средняя
 *
 * Построить прямую по отрезку
 */
fun lineBySegment(s: Segment): Line = lineByPoints(s.begin, s.end)

/**
 * Средняя
 *
 * Построить прямую по двум точкам
 */
fun lineByPoints(a: Point, b: Point): Line =
    (Point(0.0, a.y)
        .distance(Point(0.0, b.y)) / Point(a.x, 0.0)
        .distance(Point(b.x, 0.0)) * sign((b.y - a.y) / (b.x - a.x)))
        .let {
            Line(a, removePeriod(atan(it)))
        }

/**
 * Сложная
 *
 * Построить серединный перпендикуляр по отрезку или по двум точкам
 */
fun bisectorByPoints(a: Point, b: Point): Line =
    Line(Segment(a, b).midpoint, removePeriod(PI / 2 + lineByPoints(a, b).angle))

/**
 * Средняя
 *
 * Задан список из n окружностей на плоскости. Найти пару наименее удалённых из них.
 * Если в списке менее двух окружностей, бросить IllegalArgumentException
 */
fun findNearestCirclePair(vararg circles: Circle): Pair<Circle, Circle> {
    require(circles.size >= 2)

    val found = circles.groupBy({ it }, { (circles.toList() - it).toSet() })
        .map { entry ->
            entry.key to entry.value[0].map { circle ->
                circle to circle.distance(entry.key)
            }.minBy { it.second }
        }.toMap().minBy { it.value!!.second }!!

    return Pair(found.key, found.value!!.first)
}

/**
 * Сложная
 *
 * Дано три различные точки. Построить окружность, проходящую через них
 * (все три точки должны лежать НА, а не ВНУТРИ, окружности).
 * Описание алгоритмов см. в Интернете
 * (построить окружность по трём точкам, или
 * построить окружность, описанную вокруг треугольника - эквивалентная задача).
 */
fun circleByThreePoints(a: Point, b: Point, c: Point): Circle =
    bisectorByPoints(a, b).crossPoint(bisectorByPoints(a, c)).let {
        Circle(it, it.distance(b))
    }

/**
 * Очень сложная
 *
 * Дано множество точек на плоскости. Найти круг минимального радиуса,
 * содержащий все эти точки. Если множество пустое, бросить IllegalArgumentException.
 * Если множество содержит одну точку, вернуть круг нулевого радиуса с центром в данной точке.
 *
 * Примечание: в зависимости от ситуации, такая окружность может либо проходить через какие-либо
 * три точки данного множества, либо иметь своим диаметром отрезок,
 * соединяющий две самые удалённые точки в данном множестве.
 */

/**
 * Реализовал этот алгоритм:
 * http://algolist.manual.ru/maths/geom/misc/mincircle.php
 * https://neerc.ifmo.ru/wiki/index.php?title=Минимальная_охватывающая_окружность_множества_точек
 *
 * Наивную реализацию мне так и не удалось довести до конца.
 */

fun minContainingCircle(vararg points: Point): Circle {
    require(points.isNotEmpty())

    if (points.size == 1) {
        return Circle(points[0], 0.0)
    }

    val p = points.toSet().shuffled()
    var c = circleByDiameter(Segment(p[0], p[1]))

    for (i in 2..p.lastIndex) {
        if (!c.contains(p[i])) {
            c = minCircleWithPoint(p.take(i), p[i])
        }
    }

    return c
}

fun minCircleWithPoint(points: List<Point>, point: Point): Circle {
    val p = points.shuffled()
    var c = circleByDiameter(Segment(p[0], point))

    for (i in 1..p.lastIndex) {
        if (!c.contains(p[i])) {
            c = minCircleWithTwoPoints(p.take(i), p[i], point)
        }
    }

    return c
}

fun minCircleWithTwoPoints(points: List<Point>, point1: Point, point2: Point): Circle {
    val p = points.shuffled()
    var c = circleByDiameter(Segment(point1, point2))

    p.forEach {
        if (!c.contains(it)) {
            c = circleByThreePoints(it, point1, point2)
        }
    }

    return c
}