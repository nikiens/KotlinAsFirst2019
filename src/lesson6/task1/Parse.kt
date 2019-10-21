@file:Suppress("UNUSED_PARAMETER", "ConvertCallChainIntoSequence")

package lesson6.task1

import lesson2.task2.daysInMonth
import kotlin.IllegalArgumentException

/**
 * Пример
 *
 * Время представлено строкой вида "11:34:45", содержащей часы, минуты и секунды, разделённые двоеточием.
 * Разобрать эту строку и рассчитать количество секунд, прошедшее с начала дня.
 */
fun timeStrToSeconds(str: String): Int {
    val parts = str.split(":")
    var result = 0
    for (part in parts) {
        val number = part.toInt()
        result = result * 60 + number
    }
    return result
}

/**
 * Пример
 *
 * Дано число n от 0 до 99.
 * Вернуть его же в виде двухсимвольной строки, от "00" до "99"
 */
fun twoDigitStr(n: Int) = if (n in 0..9) "0$n" else "$n"

/**
 * Пример
 *
 * Дано seconds -- время в секундах, прошедшее с начала дня.
 * Вернуть текущее время в виде строки в формате "ЧЧ:ММ:СС".
 */
fun timeSecondsToStr(seconds: Int): String {
    val hour = seconds / 3600
    val minute = (seconds % 3600) / 60
    val second = seconds % 60
    return String.format("%02d:%02d:%02d", hour, minute, second)
}

/**
 * Пример: консольный ввод
 */
fun main() {
    println("Введите время в формате ЧЧ:ММ:СС")
    val line = readLine()
    if (line != null) {
        val seconds = timeStrToSeconds(line)
        if (seconds == -1) {
            println("Введённая строка $line не соответствует формату ЧЧ:ММ:СС")
        } else {
            println("Прошло секунд с начала суток: $seconds")
        }
    } else {
        println("Достигнут <конец файла> в процессе чтения строки. Программа прервана")
    }
}

val months = listOf(
    "", "января", "февраля", "марта", "апреля", "мая", "июня",
    "июля", "августа", "сентября", "октября", "ноября", "декабря"
)

/**
 * Средняя
 *
 * Дата представлена строкой вида "15 июля 2016".
 * Перевести её в цифровой формат "15.07.2016".
 * День и месяц всегда представлять двумя цифрами, например: 03.04.2011.
 * При неверном формате входной строки вернуть пустую строку.
 *
 * Обратите внимание: некорректная с точки зрения календаря дата (например, 30.02.2009) считается неверными
 * входными данными.
 */
fun dateStrToDigit(str: String): String =
    Regex("""\b(\d{1,2})\s([а-я]+)\s(\d+)\b""").matchEntire(str)
        ?.destructured
        ?.let { (day, month, year) ->
            val monthNum = months.indexOf(month)

            if (monthNum < 0 || day.toInt() > daysInMonth(monthNum, year.toInt()))
                return ""

            "%02d.%02d.%s".format(day.toInt(), monthNum, year)
        }
        ?: ""

/**
 * Средняя
 *
 * Дата представлена строкой вида "15.07.2016".
 * Перевести её в строковый формат вида "15 июля 2016".
 * При неверном формате входной строки вернуть пустую строку
 *
 * Обратите внимание: некорректная с точки зрения календаря дата (например, 30 февраля 2009) считается неверными
 * входными данными.
 */
fun dateDigitToStr(digital: String): String =
    Regex("""\b((?!00)\d{2})\.((?!00)\d{2})\.(\d+)\b""").matchEntire(digital)
        ?.destructured
        ?.let { (day, month, year) ->
            val monthName = months.getOrNull(month.toInt()) ?: return ""

            if (day.toInt() > daysInMonth(month.toInt(), year.toInt()))
                return ""

            "%d %s %s".format(day.toInt(), monthName, year)
        }
        ?: ""

/**
 * Средняя
 *
 * Номер телефона задан строкой вида "+7 (921) 123-45-67".
 * Префикс (+7) может отсутствовать, код города (в скобках) также может отсутствовать.
 * Может присутствовать неограниченное количество пробелов и чёрточек,
 * например, номер 12 --  34- 5 -- 67 -89 тоже следует считать легальным.
 * Перевести номер в формат без скобок, пробелов и чёрточек (но с +), например,
 * "+79211234567" или "123456789" для приведённых примеров.
 * Все символы в номере, кроме цифр, пробелов и +-(), считать недопустимыми.
 * При неверном формате вернуть пустую строку.
 *
 * PS: Дополнительные примеры работы функции можно посмотреть в соответствующих тестах.
 */
fun flattenPhoneNumber(phone: String): String =
    Regex("""^(\+\d+)?\s*((?:\(\d+[\d\s\-]*\))?[\s\d-]*)$""").matchEntire(phone)
        ?.destructured
        ?.let { (prefix, phone) ->
            prefix + phone.replace(Regex("""[\s-()]"""), "")
        }
        ?: ""

/**
 * Средняя
 *
 * Результаты спортсмена на соревнованиях в прыжках в длину представлены строкой вида
 * "706 - % 717 % 703".
 * В строке могут присутствовать числа, черточки - и знаки процента %, разделённые пробелами;
 * число соответствует удачному прыжку, - пропущенной попытке, % заступу.
 * Прочитать строку и вернуть максимальное присутствующее в ней число (717 в примере).
 * При нарушении формата входной строки или при отсутствии в ней чисел, вернуть -1.
 */
fun bestLongJump(jumps: String): Int =
    Regex("""^[\s\d-%]*$""").matchEntire(jumps)
        ?.value
        ?.let { result ->
            Regex("""\b\d+\b""").findAll(result).map { it.groupValues[0].toInt() }.max()
        }
        ?: -1

/**
 * Сложная
 *
 * Результаты спортсмена на соревнованиях в прыжках в высоту представлены строкой вида
 * "220 + 224 %+ 228 %- 230 + 232 %%- 234 %".
 * Здесь + соответствует удачной попытке, % неудачной, - пропущенной.
 * Высота и соответствующие ей попытки разделяются пробелом.
 * Прочитать строку и вернуть максимальную взятую высоту (230 в примере).
 * При нарушении формата входной строки, а также в случае отсутствия удачных попыток,
 * вернуть -1.
 */
fun bestHighJump(jumps: String): Int =
    Regex("""^[\s\d-%+]*$""").matchEntire(jumps)
        ?.value
        ?.let { result ->
            Regex("""\b(\d+)\s\+""").findAll(result).map { it.groupValues[1].toInt() }.max()
        }
        ?: -1

/**
 * Сложная
 *
 * В строке представлено выражение вида "2 + 31 - 40 + 13",
 * использующее целые положительные числа, плюсы и минусы, разделённые пробелами.
 * Наличие двух знаков подряд "13 + + 10" или двух чисел подряд "1 2" не допускается.
 * Вернуть значение выражения (6 для примера).
 * Про нарушении формата входной строки бросить исключение IllegalArgumentException
 */
fun plusMinus(expression: String): Int =
    Regex("""^-?\d+(\s[+-]\s\d+)*\1*""").matchEntire(expression)
        ?.value
        ?.let { result ->
            Regex("""^-?\d+\s?|(?:(?:-?|\+)?\s\d+)\s*""").findAll(result)
                .map { it.groupValues[0].replace(" ", "").toInt() }.sum()
        }
        ?: throw IllegalArgumentException()

/**
 * Сложная
 *
 * Строка состоит из набора слов, отделённых друг от друга одним пробелом.
 * Определить, имеются ли в строке повторяющиеся слова, идущие друг за другом.
 * Слова, отличающиеся только регистром, считать совпадающими.
 * Вернуть индекс начала первого повторяющегося слова, или -1, если повторов нет.
 * Пример: "Он пошёл в в школу" => результат 9 (индекс первого 'в')
 */
fun firstDuplicateIndex(str: String): Int =
    Regex("""\b([а-яa-z]+)\s\1\b""").find(str.toLowerCase())?.range?.first ?: -1

/**
 * Сложная
 *
 * Строка содержит названия товаров и цены на них в формате вида
 * "Хлеб 39.9; Молоко 62; Курица 184.0; Конфеты 89.9".
 * То есть, название товара отделено от цены пробелом,
 * а цена отделена от названия следующего товара точкой с запятой и пробелом.
 * Вернуть название самого дорогого товара в списке (в примере это Курица),
 * или пустую строку при нарушении формата строки.
 * Все цены должны быть больше либо равны нуля.
 */
fun mostExpensive(description: String): String =
    Regex("""^(?:(?:.+\s\d+\.?\d*);?\s?)*\b""").matchEntire(description)?.value
        ?.split("; ")?.groupBy({ it.split(" ")[0] }, { it.split(" ")[1].toDouble() })
        ?.maxBy { it.value[0] }?.key
        ?: ""

/**
 * Сложная
 *
 * Перевести число roman, заданное в римской системе счисления,
 * в десятичную систему и вернуть как результат.
 * Римские цифры: 1 = I, 4 = IV, 5 = V, 9 = IX, 10 = X, 40 = XL, 50 = L,
 * 90 = XC, 100 = C, 400 = CD, 500 = D, 900 = CM, 1000 = M.
 * Например: XXIII = 23, XLIV = 44, C = 100
 *
 * Вернуть -1, если roman не является корректным римским числом
 */
fun fromRoman(roman: String): Int {
    var num = Regex("""\bM{0,4}(?:CM|CD|D?C{0,3})(?:XC|XL|L?X{0,3})(?:IX|IV|V?I{0,3})\b""")
        .matchEntire(roman)?.value ?: return -1

    val numeral = mapOf(
        "M" to 1000, "CM" to 900, "D" to 500, "CD" to 400,
        "C" to 100, "XC" to 90, "L" to 50, "XL" to 40,
        "X" to 10, "IX" to 9, "V" to 5, "IV" to 4, "I" to 1
    )

    var pos = 0
    var res = 0

    while (num.isNotEmpty()) {
        val sym = numeral.keys.elementAt(pos)

        if (num.startsWith(sym)) {
            res += numeral.getValue(sym)
            num = num.substring(sym.length)
        } else
            pos++
    }

    return res
}

/**
 * Очень сложная
 *
 * Имеется специальное устройство, представляющее собой
 * конвейер из cells ячеек (нумеруются от 0 до cells - 1 слева направо) и датчик, двигающийся над этим конвейером.
 * Строка commands содержит последовательность команд, выполняемых данным устройством, например +>+>+>+>+
 * Каждая команда кодируется одним специальным символом:
 *	> - сдвиг датчика вправо на 1 ячейку;
 *  < - сдвиг датчика влево на 1 ячейку;
 *	+ - увеличение значения в ячейке под датчиком на 1 ед.;
 *	- - уменьшение значения в ячейке под датчиком на 1 ед.;
 *	[ - если значение под датчиком равно 0, в качестве следующей команды следует воспринимать
 *  	не следующую по порядку, а идущую за соответствующей следующей командой ']' (с учётом вложенности);
 *	] - если значение под датчиком не равно 0, в качестве следующей команды следует воспринимать
 *  	не следующую по порядку, а идущую за соответствующей предыдущей командой '[' (с учётом вложенности);
 *      (комбинация [] имитирует цикл)
 *  пробел - пустая команда
 *
 * Изначально все ячейки заполнены значением 0 и датчик стоит на ячейке с номером N/2 (округлять вниз)
 *
 * После выполнения limit команд или всех команд из commands следует прекратить выполнение последовательности команд.
 * Учитываются все команды, в том числе несостоявшиеся переходы ("[" при значении под датчиком не равном 0 и "]" при
 * значении под датчиком равном 0) и пробелы.
 *
 * Вернуть список размера cells, содержащий элементы ячеек устройства после завершения выполнения последовательности.
 * Например, для 10 ячеек и командной строки +>+>+>+>+ результат должен быть 0,0,0,0,0,1,1,1,1,1
 *
 * Все прочие символы следует считать ошибочными и формировать исключение IllegalArgumentException.
 * То же исключение формируется, если у символов [ ] не оказывается пары.
 * Выход за границу конвейера также следует считать ошибкой и формировать исключение IllegalStateException.
 * Считать, что ошибочные символы и непарные скобки являются более приоритетной ошибкой чем выход за границу ленты,
 * то есть если в программе присутствует некорректный символ или непарная скобка, то должно быть выброшено
 * IllegalArgumentException.
 * IllegalArgumentException должен бросаться даже если ошибочная команда не была достигнута в ходе выполнения.
 *
 */
fun balancedBrackets(cmds: String): Boolean {
    var count = 0

    cmds.forEach {
        when (it) {
            '[' -> count++
            ']' -> count--
        }
        if (count < 0) return false
    }

    return count == 0
}

fun getCloseBracketStep(cmds: String, step: Int): Int {
    var count = 0

    for (i in step..cmds.lastIndex) {
        when (cmds[i]) {
            '[' -> count++
            ']' -> count--
        }
        if (count == 0) return i
    }
    return 0
}

fun getOpenBracketStep(cmds: String, step: Int): Int {
    var count = 0

    for (i in step downTo 0) {
        when (cmds[i]) {
            ']' -> count++
            '[' -> count--
        }
        if (count == 0) return i
    }
    return 0
}

fun computeDeviceCells(cells: Int, commands: String, limit: Int): List<Int> {
    val cmds = Regex("""^[+\-><\[\]\s]*$""").matchEntire(commands)
        ?.value ?: throw IllegalArgumentException()

    require(balancedBrackets(cmds))

    val res = MutableList(cells) { 0 }
    var pos = cells / 2

    var step = 0
    var lim = 0

    while (lim < limit && step <= cmds.lastIndex) {
        check(pos in 0..res.lastIndex)

        when (cmds[step]) {
            '>' -> pos++
            '<' -> pos--
            '+' -> res[pos]++
            '-' -> res[pos]--
            '[' -> if (res[pos] == 0) step = getCloseBracketStep(cmds, step)
            ']' -> if (res[pos] != 0) step = getOpenBracketStep(cmds, step)
            else -> Unit
        }
        step++
        lim++
    }

    check(pos in 0..res.lastIndex)
    return res
}
