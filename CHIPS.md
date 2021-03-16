# CHIPS: Chisel Hardware Property Specification Language

The CHIPS language is a domain specific language (DSL) embedded into the [Scala](https://www.scala-lang.org/) programming language.
The core language features of CHIPS are heavily inspired by the SystemVerilog Assertion Language (SVA), described in the SystemVerilog Standard [IEEE 1800-2017](https://standards.ieee.org/standard/1800-2017.html).
The main goal of the CHIPS specification language is to formally specify properties in digital hardware design generators. The primary benefit is that
assertions help to detect functional bugs early in the design phase and can help alleviate verification shortcomings in agile hardware design methodologies.

## CHIPS Assertions

One goal in the CHIPS DSL design is to stay as close to SVAs as possible so verification engineers do not need to learn a completely new language.
The CHIPS DSL resembles the SVA syntax with small adjustments. All CHIPS programs can be synthesized to SVAs.

### Assertion Kinds

CHIPS supports three kinds of assertions analogous to SystemVerilog assertions.

**Immediate assertions:**
Immediate assertions are non-temporal and act as procedural `if` statements.
In SystemVerilog code, these assertions are prone to produce spurious failures due to simulation races.

**Deferred assertions:**
Deferred assertions are similar to immediate assertions in the sense that they are also non-temporal, but they do not produce spurious failures.
Deferred assertions can be placed both inside and outside procedural SystemVerilog code.
They are further subdivided into `observed` and `final` assertions.

**Concurrent assertions:**
Concurrent assertions are temporal and thus must always be clocked.
They can be located inside and outside procedural code.
Concurrent assertions use sampled values of their variables.

The example shows all assertions kinds, and their position in SystemVerilog code.

```SystemVerilog
module m2(input logic c, d, clk);
  logic a, b;
  alwyas_comb begin
    a = c & d;
    b = c | d;

    // immediate assertions
    a1: assert (a -> b);

    // deferred assertion
    a2: assert final (a -> b);

    // concurrent assertion
    a3: assert property (@clk a != b);
  end

  // deferred assertion
  a4: assert final (a -> b);

  // concurrent assertion
  a5: assert property (@clk a != b);
endmodule : m2
```

### Assertion Statements

An assertion statement in SystemVerilog can be described by the abstract syntax:
> `assertion ::= id : assert_keyword (assertion_body) action_block`

The `assert_keyword` can be one of the following kinds: `assert`, `assume`, `cover`, `restrict`

In the CHIPS language all SystemVerilog assertion (SVA) statements are expressed through Scala functions of type `f : (Option[String] => ...)`.
The first function parameter is an optional assertion `id: Option[String]`.
The table gives an overview, and the relation between CHIPS assertions and SVA statements.

```
+-----------+---------------------------------+-------------------+---------------------------------------------+
| Statement |               Kind              |     SVA Syntax    |                    CHIPS                    |
+===========+===========+=====================+===================+=============================================+
|           |           |        simple       |            assert |  Assert.immediate : (Option[String] => ...) |
|           |           +----------+----------+-------------------+---------------------------------------------+
|           | immediate |          | observed |         assert #0 |   Assert.observed : (Option[String] => ...) |
|   assert  |           | deferred +----------+-------------------+---------------------------------------------+
|           |           |          |   final  |      assert final |    Assert.`final` : (Option[String] => ...) |
|           +-----------+----------+----------+-------------------+---------------------------------------------+
|           |            concurrent           |   assert property |   Assert.property : (Option[String] => ...) |
+-----------+-----------+---------------------+-------------------+---------------------------------------------+
|           |           |        simple       |            assume |  Assume.immediate : (Option[String] => ...) |
|           |           +----------+----------+-------------------+---------------------------------------------+
|           | immediate |          | observed |         assume #0 |   Assume.observed : (Option[String] => ...) |
|   assume  |           | deferred +----------+-------------------+---------------------------------------------+
|           |           |          |   final  |      assume final |    Assume.`final` : (Option[String] => ...) |
|           +-----------+----------+----------+-------------------+---------------------------------------------+
|           |            concurrent           |   assume property |   Assume.property : (Option[String] => ...) |
+-----------+-----------+---------------------+-------------------+---------------------------------------------+
|           |           |        simple       |             cover |   Cover.immediate : (Option[String] => ...) |
|           |           +----------+----------+-------------------+---------------------------------------------+
|           | immediate |          | observed |          cover #0 |    Cover.observed : (Option[String] => ...) |
|   cover   |           | deferred +----------+-------------------+---------------------------------------------+
|           |           |          |   final  |       cover final |     Cover.`final` : (Option[String] => ...) |
|           +-----------+----------+----------+-------------------+---------------------------------------------+
|           |            concurrent           |    cover property |    Cover.property : (Option[String] => ...) |
+-----------+---------------------------------+-------------------+---------------------------------------------+
| restrict  |            concurrent           | restrict property | Restrict.property : (Option[String] => ...) |
+-----------+---------------------------------+-------------------+---------------------------------------------+
```

The example shows a comparison between SVAs and CHIPS assertions.

```Scala
import chips._

// immediate assertions
// a1: assert (a -> b);
Assert.immediate("a1") {
  a -> b
}

// deferred assertion
// a2: assert final (a -> b);
Assert.`final`("a2") {
  a -> b
}

// concurrent assertion
// a3: assert property (@clk a != b);
Assert.property("a3") {
  a != b
}
```

### Immediate Assertions

Immediate assertions are Boolean and unclocked making them the simplest kind of assertions.
They can only be placed in procedural code.

### Deferred Assertions

Deferred immediate assertions are a variant of immediate assertions and subject to the same constraints.
They are Boolean and unclocked but they can be placed both inside and outside procedural code.

### Concurrent Assertions

Concurrent assertions are always clocked and all assertion evaluation attempts are synchronized with this event.
Values inside concurrent assertions are sampled at the beginning of each global synchronization even (global clock).
The global clock is either explicitly defined or inferred from the context.

TODO: add clocking, gated clocking and reset description

#### Clock

#### Gated Clock

#### Global Clock

#### Reset

#### Boolean Expressions

Boolean expressions represented by the ADTs `PBoolExpr[T]` and `SBoolExpr[T]` are fundamental building blocks for assertions.
They are used in property and sequence operators as operands.
In CHIPS, Boolean expressions are polymorphic types that can be understood as high level wrappers around language dependent Boolean expressions.

In SystemVerilog for example, Boolean expressions are composed of SystemVerilog expression with certain restrictions on the operators, referenced variables and their types.
One of these restrictions is that evaluation of expressions has no side effects.
One need to consider these restrictions if SVAs synthesized from CHIPS expressions are used in SystemVerilog code.

### Assumptions

Assumptions are constraints on the environment and document conditions that are required for proper functionality of a design module.
Whereas assertions specify the behavior of a design under test, assumptions specify the behavior of its environment.

### Restrictions

Restrictions are used in formal verification to limit formal proofs to particular cases.
They have only concurrent form, and are treated as assumptions in formal verification but are completely ignored in simulation.

### Coverage

With *functional coverage* one can describe how a design under test *can* behave (cf. assertions define how a DUT *must* behave).

## CHIPS Properties

A property defines its own language.
A property is a **temporal** formula that can be either true or false on a given trace.
Therefore, properties are interpreted over traces.
Properties are built from simpler properties in a recursive manner.
Property expressions in CHIPS are represented by the abstract data type (ADT) `PExpr`.

In CHIPS property expressions are manipulated by property operators.
All binary operators on properties are **left associative**.
This is a restriction of the DSL embedding into the Scala metalanguage.
Setting brackets correctly is therefore indispensable.
All CHIPS property operators can also be used in **prefix notation**, also known as Polish notation, by adding the import: `import chips.PrefixOps._`.

### Basic Properties

The table gives an overview over the most common property operators in CHIPS.
Temporal operators can have a linear temporal logic (LTL) alias that can be used instead to describe properties in a more dense form.

| Operator         | Signature                    | LTL Alias | Description                                                                                                                                                            |
| --------------   | ---------------------------- | --------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| `not p`          | `PExpr => PExpr`             | -         | property negation                                                                                                                                                      |
| `nexttime p`     | `PExpr => PExpr`             | -         | `nextime p` is *true in clock tick i* <br> iff `p` is true in clock tick *i+1*                                                                                         |
| `p and q`        | `PExpr => PExpr => PExpr`    | -         | property conjunction                                                                                                                                                   |
| `p or q`         | `PExpr => PExpr => PExpr`    | -         | property disjunction                                                                                                                                                   |
| `p until q`      | `PExpr => PExpr => PExpr`    | `U`       | `p until q` is *true in clock tick i* <br> iff `p` is true in every clock tick *j >= i* until, but not including, <br> the first clock tick *k >= i* where `q` is true |
| `p until_with q` | `PExpr => PExpr => PExpr`    | -         | `p until_with q` is *true* <br> iff `p` is *true* in every clock tick until (and including) <br> the first clock tick where `q` is *true*                              |
| `always p`       | `PExpr => PExpr`             | `G`       | `always p` is *true in clock tick i* <br> iff `p` is true in all clock ticks *j >= i*                                                                                  |
| `s_eventually p` | `PExpr => PExpr`             | `sF`      | `s_eventually p` is *true in clock tick i* <br> iff `p` is true in some clock tick *j >= i*                                                                            |

Complex properties can be built from simpler ones in a recursively by applying unary or binary operators.
Primitive properties are constructed from sequences.

#### Boolean Property

TODO

#### `nexttime` Property

TODO

#### `always` Property

TODO

#### `s_eventually` Property

TODO

#### Boolean Property Connectives

TODO

#### `until` Property

TODO

#### Boolean property operators

TODO

Property operators express Boolean connectives between properties.

| Operator  | Signature                              | Description    |
| --------- | -------------------------------------- | -------------- |
| `not`     | `PExpr => PExpr`                       | negation       |
| `or`      | `PExpr => PExpr => PExpr`              | disjunction    |
| `and`     | `PExpr => PExpr => PExpr`              | conjunction    |
| `implies` | `PExpr => PExpr => PExpr`              | implication    |
| `iff`     | `PExpr => PExpr => PExpr`              | equivalence    |
| `ite`     | `PBoolExpr => PExpr => PExpr => PExpr` | if conditional |

#### Unbounded Linear Temporal Operators (LTL)

| Operator       | Signature                 | LTL Alias | Description |
| -------------- | ------------------------- | --------- | ----------- |
| `until`        | `PExpr => PExpr => PExpr` | `U`       |             |
| `until_with`   | `PExpr => PExpr => PExpr` | -         |             |
| `s_until`      | `PExpr => PExpr => PExpr` | `sU`      |             |
| `s_until_with` | `PExpr => PExpr => PExpr` | -         |             |
| `always`       | `PExpr => PExpr`          | `G`       |             |
| `s_eventually` | `PExpr => PExpr`          | `sF`      |             |

#### Bounded Linear Temporal Operators (LTL)

Useful for specifying properties that must be satisfied within some specified range of clock ticks.

| Operator             | Signature        | LTL Alias | Description |
| -------------------- | ---------------- | --------- | ----------- |
| `nexttime[n]`        | `PExpr => PExpr` | `X`       |             |
| `s_nexttime[n]`      | `PExpr => PExpr` | `sX`      |             |
| `eventually[m,n]`    | `PExpr => PExpr` | `F`       |             |
| `s_eventually[m,n]`  | `PExpr => PExpr` | `sF`      |             |
| `always [m,n]`       | `PExpr => PExpr` | `G`       |             |
| `s_always [m,n]`     | `PExpr => PExpr` | `sG`      |             |

---

## CHIPS Sequences

A sequence is a rule defining a series of values in time.
Like a property, a sequence defines a language, namely, the set of words (traces) that match the sequence.
Unlike the language of a property, the language of a sequence is finitary.

A sequence does not have a truth value, it has one initial point and zero or more match, or tight satisfaction points.
Like properties, sequences are clocked. A sequence expression in CHIPS is represented by the ADT `SExpr`.

### Basic Sequences

| Operator    | Signature                 | Alias      | Description                        |
| ----------- | ------------------------- | ---------- | ---------------------------------- |
| `rep[n]`    | `SExpr => SExpr`          | -          | bounded sequence repetition        |
| `rep[0, $]` | `SExpr => SExpr`          | `rep[*]`   | zero or more repetition            |
| `rep[1, $]` | `SExpr => SExpr`          | `rep[+]`   | one or more repetition             |
| `delay[n]`  | `SExpr => SExpr => SExpr` | `##[n]`    | bounded sequence delay             |
| `or`        | `SExpr => SExpr => SExpr` | -          | sequence disjunction               |
| &#124; ->   | `SExpr => PExpr => PExpr` | -          | overlapping suffix implication     |
| &#124; =>   | `SExpr => PExpr => PExpr` | -          | non-overlapping suffix implication |

#### Boolean Sequence

Boolean expressions define Boolean sequences expressions represented by `SBoolExpr`.
A Boolean sequence has a match at its initial point if the encapsulated Boolean expression is true, otherwise, it does not have any satisfaction points at all.

#### Sequential Property

Although sequences cannot be `true` or `false`, they can be associated with a *sequential property*.
In CHIPS sequences are promoted to sequential properties when they are used in a property context.

#### Sequence Concatenation

A new sequence can be built from two sequences `r` and `s` by sequence concatenation using the `delay[1]` operator, or it's alias `##[1]`.
Overlapping concatenation or *sequence fusion* is achieved via `r.delay[0](s)` or `r ##[0] s`.
When two sequences are adjoining, and there is a constant number of clock ticks between them, there is a special syntax capturing this situation: `r.delay[n](s)` or `r ##[n] s`.
An initial sequence delay is specified using the `##/[n]` operator.

```Scala
import chips.ast._

val r = SBoolExpr(true)
val s = SBoolExpr(false)

// 1) sequence concatenation
val result1 = r.delay[1](s)
val result1_s = r.##[1](s)

// 2) sequence fusion
val result2 = r.delay[0](s)
val result2_s = r.##[0](s)

// 3) multiple delays
val result3 = r.delay[2](s)
val result3_s = r.##[2](s)

// 4) initial delay
val result4 = r.##/[10]
```

#### Suffix Implication

Sequences are promoted to properties if they are used in a property context.
Another way to create properties from sequences is to use suffix implications.
A *suffix implication* is built from a sequence `s`, called the *antecedent*, and a property `p`, called the *consequent*.
There are two versions: *overlapping*, denoted by `s |-> p` and nonoverlapping, denoted by `s |=> p`.
In the overlapping case the consequent is checked starting from every nonempty match of the antecedent.
In the nonoverlapping case the consequent is checked started from the next clock tick after each match of the antecedent.
If the antecedent does not have a match the suffix implication holds, which is called *vacuous execution*.

#### Consecutive Repetition

Operator `rep[n]` denotes a consecutive repetition of a sequence `s` n times.
It is possible to define a `s.rep[0]` sequence, which denotes a sequence admitting only an empty match.
In other words, `s.rep[0]` matches any sequences and is called the *empty sequence*.
The empty sequence cannot be promoted to a sequential property.

**Example:** Signal `sig` remains high during five cycles.

```Scala
import chips.ast._

val sig = SBoolExpr("sig")
val n_sig = SBoolExpr("!sig")

val property = (n_sig.##[1](sig)) |=> sig.rep[4]
```

#### Sequence Disjunction

A `sequence disjunction` `r or s` is a sequence which has a match whenever either `r` or `s` (or both) have a match.

#### Unbounded Sequences

TODO

---

## Advanced Assertions

### Advanced Properties

#### Sequential Property

Sequential properties can be `weak` or `strong`.

#### Boolean Property Operators

TODO

#### Suffix Operators

##### Suffix Implication

TODO

##### Suffix Conjunction (Followed-By)

TODO

#### Unbounded Linear Temporal Operators

TODO

### Advanced Sequences

#### Sequence Operators

Sequence operators build new sequences from its operands (compositions).

| Operator      | Signature                     | Alias        | Description                                                               |
| ------------- | -------------------------     | ------------ | -----------                                                               |
| `goto[n]`     | `PBoolExpr => PExpr => PExpr` |`->`          | goto repetition                                                           |
| `nrep`        | TODO                          |              | nonconsecutive repetition                                                 |
| `throughout`  | `PBoolExpr => PExpr => PExpr` |              | makes sure that a Boolean condition <br> holds through the whole sequence |
| `within`      | TODO                          |              | sequence containment                                                      |
| `intersect`   | TODO                          |              | sequence intersection                                                     |
| `and`         | `SExpr => SExpr => SExpr`     |              | sequence conjunction                                                      |
| `or`          | `SExpr => SExpr => SExpr`     |              | sequence disjunction                                                      |
| `first_match` | TODO                          |              | first match                                                               |

##### Throughout

Throughout is a convenience operator (syntactic sugar) without additional expressive power.
`e throughout s` has a match in clock tick *t* iff `s` has a match at *t*, and in each clock tick from the start of the evaluation of `s` until and including the match of `s`, the condition `e` is *true*.

```Scala
import chips.ast._

// Req: 3 consecutive enabled occurrences of read followed by four enabled occurrences of write
val seq1 = (read && en).rep[3].delay[1]((write && en).rep[4])

// using throughout operator
val seq2 = en throughout {
  read.rep[3].delay[1](write.rep[4])
}

```

##### Goto Repetition

TODO

##### Nonconsecutive Repetition

TODO

##### Intersection

TODO

##### Sequence Conjunction

TODO

##### Sequence Containment

TODO

##### First Match of a Sequence

TODO

##### Sequence Methods

In CHIPS sequence methods are always in **post-fix notation**.

| Method      | Signature               | Description |
| ----------- | ----------------------- | ----------- |
| `triggered` | `SExpr => SExprSMethod` | TODO        |
| `matched`   | `SExpr => SExprSMethod` | TODO        |


### Resets

A reset condition is a condition upon which it is desired to stop evaluation of a concurrent assertion or sub-property in a preemptive or abortive way.

- aysnc resets: `disable iff`, `accept_on`, `reject on`
- sync resets: `sync_accept_on`, `sync_reject_on`

#### Disable Clause

`disable iff`

#### Aborts

TODO

| Operator         | Signature                                     | Description |
| --------         | ---------                                     | ----------- |
| `accept_on`      | `accept_on :: AbortCondition => prop => prop` | async abort |
| `reject_on`      | `accept_on :: AbortCondition => prop => prop` | async abort |
| `sync_accept_on` | `accept_on :: AbortCondition => prop => prop` | sync abort  |
| `sync_reject_on` | `accept_on :: AbortCondition => prop => prop` | sync abort  |

#### Examples

SVA:

```
a_disable: assert property (
  disable iff (reset)
  @(posedge clk) a |=> b
) else $error("FAIL")
```

---

## Examples for Transaction Level Properties

### Bus infrastructure checks

**[Req1]**
> "Whenever a master module initiates a transaction with address Y, the corresponding transaction is executed
> on the module where Y lies in the address range of that module!"

**[Req2]**
> "A bus response to a specific request is never issued later than 50 nanoseconds!"

**[Req3]**
> "A request is always responded before another request is placed!"

**[Req4]**
> "Each request should be granted in four clock cycles"

```SystemVerilog
prop4: assert property (@(posedge clk) req |-> nexttime[4] grant);
```

### Dataflow checks

**[Req1]**
> "A write attempt to a SW visible register implies that the payload is stored into that register once the transaction
> has finished!"
>
**[Req2]**
> "If data is written to a register in an output device, it is required that this data is transported out as soon as
> the environment is ready!"
>
**[Req3]**
> "If data is written to a buffer, it is required that this data flows out within a maximum amount of time!"

### Control flow checks

**[Req1]**
> "Correct occurrence of data-dependent packet requests!"
>
**[Req2]**
> "The execution of a specific instruction implies the correct sequence of memory-fetch and IO-transactions!"

### Software accesses

**[Req1]**
> "No write attempt to a read-only register occurs!"
>
**[Req2]**
> "No write / read attempt to a full / empty buffer exists!"

### Configurations

**[Req1]**
> "A firing interrupt implies that the interrupt was enabled!"

---

## ESL verification requirements

- sequences: behavior of a model over time considered as a trace of all state and signal values over time
    - transaction sequences
    - event sequences
    - `type ::= Match | Failed`
    - formal models: omega automaton, Buechi automaton (deterministic), Kripke structure, labeled transition system (
      LTS)
- boolean propositions
    - `type ::= True | False`
- transaction concept
- event construct
- temporal logic: `always, never, eventually, next`
    - delay operators: zero step, multi step, interval range
        - event sensitivity (e.g., clock, signal)

- **let** formalism
    - local expressions and variables

- assertions for High Level Models
    - SVA: **checker** concept

- reset constructs

### General aspects

- PSL and SVA evaluation semantics
- assertion coverage support
- local variables
- control mechanisms to turn on/off assertions
- severity levels for assertion failures
- composition support (assertion libraries)
    - package concept
- externalizing assertions (bind construct)
- declarative: assertions describe what to check
- default clocking via implicit `clocking blocks`
- interface concept
    - data types and variables
    - clocking blocks, functions and tasks
    - initial, always and final procedures to define additional behavior
    - sequences, properties and assertions
- `action_block` concept
    - `pass action`
    - `fail action`
- global clocking concept: specify primary clock for formal verification
- `restrict` concept
    - limit formal proof to special case
    - ignore on simulation level
- functional coverage concept
    - methodology to specify the scenarios to be covered
    - FV: `coverage point` condition is checked for its feasibility (reachability)
- assertions on interfaces (black-box verification): express behavior as seen at an interface
    - nonintrusive to the design units
    - can be retained physically outside the design units
    - used in system integration (consistency of interfaces)
    - examples:
        - bus communication protocols
        - memory transactions
        - data transformations
        - transaction arbitration
- assertions within the design (white-box verification): local correctness
    - examples:
        - compliance on interface
        - FSM transitions
        - memory access correctness
        - stack and queue overflow and underflow
        - arithmetic overflow
        - signal stability
- assertion coverage
    - are there enough assertions?
    - coverage based verification: refine tests so that all coverage points are hit
- equivalence verification
    - assumptions about inputs set constraints
    - assumptions about internal signals are used as hints for formal equivalence verification
    - assumptions are usually nontemporal: `assume final ...`
- timing verification
    - false path elimination
    - clock domain crossing
    - multicycle path
- post-silicon validation
    - synthesize critical assertions on-chip for runtime debugging

