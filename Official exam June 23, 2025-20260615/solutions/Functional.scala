import scala.annotation.tailrec
import scala.annotation.tailrec
import scala.collection.immutable.HashMap
import scala.collection.mutable

// Exercise 1

// Suppose the following trait with basic operations for directed graphs.

trait Graph[N] {
  def addNode(node: N): Graph[N] // adds a node to the graph
  def addEdge(from: N, to: N): Graph[N] // adds an edge from node 'from' to node 'to', throws an
  // IllegalArgumentException if any of the nodes does not exist in the graph
  def neighbors(node: N): Set[N] // returns the neighboring nodes of a given node (i.e., endpoints of
  // edges with the given node as origin)
  def hasNode(node: N): Boolean // checks if the graph contains a given node
}

class GraphOverMap[T] private (val data: Map[T, Set[T]]) extends Graph[T] :
  def this() =
    this(new HashMap[T, Set[T]]())
  override def toString =
    data.toString
  override def hashCode =
    data.hashCode
  override def equals(o: Object) =
    o match
      case g: GraphOverMap[T] => data.equals(g.data)
      case _ => false
  def addNode(node: T): Graph[T] =
    if data.isDefinedAt(node) then this
    else new GraphOverMap[T](data + (node -> Set.empty[T]))
  def addEdge(from: T, to: T): Graph[T] =
    if data.isDefinedAt(from) then new GraphOverMap[T](data - from + (from -> (data.get(from).get + to)))
    else throw new IllegalArgumentException(s"$from is not in the graph")
  def neighbors(node: T): Set[T] = data.getOrElse(node, Set.empty[T])
  def hasNode(node: T): Boolean = data.isDefinedAt(node)

def combinations[A](k: Int, list: List[A]): List[List[A]] =
  k match
    case 0 => List(Nil)
    case n if n > list.length => Nil
    case _ => list match
      case Nil => List(Nil)
      case x :: xs => (combinations(k-1, xs).map(item => x :: item)) ++ combinations(k, xs)

class Person(name: String, age: Int) {
  def getAge = age
  def getName = name
  override def toString: String = s"Person($name, $age)"
}
// and a value
val people = List(
  Person("Alice", 30), Person("Andrew", 25), Person("Charlie", 30),
  Person("Catherine", 40), Person("Eve", 35), Person("Edward", 28),
  Person("Grace", 32), Person("George", 45), Person("Ivy", 29),
  Person("Isaac", 33), Person("Karen", 27), Person("Kyle", 30),
  Person("Mona", 25), Person("Michael", 30), Person("Oscar", 36)
)

val byInitial: Map[Char, Set[String]] =
  people.foldLeft(new HashMap[Char, Set[String]]())((m, p) => m + (p.getName(0) -> (m.getOrElse(p.getName(0), Set.empty) + p.getName)))

val agesInMonthsMap: Map[String, Int] =
  people.foldLeft(new HashMap[String, Int]())((m, p) => m + (p.getName -> p.getAge*12))

val avgAge = people.map(_.getAge).sum / people.length

def partitionBy[A](list: List[A])(f: A => Boolean): (List[A], List[A]) =
  list.foldLeft((List.empty[A], List.empty[A]))(
    (acc, item) =>
      val (ls, ln) = acc
      if f(item) then
        (item :: ls, ln)
      else
        (ls, item :: ln)
)

partitionBy(List("apple","banana","cherry","date"))(_.length > 5)