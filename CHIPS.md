# CHIPS: Chisel Hardware Property Specification Language

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

### Assertion kinds

- immediate assertions
    - nontemporal
    - act as procedural `if` statements
    - prone to producing spurious failures due to simulation races
- deferred assertions
    - nontemporal
    - do not produce spurious failures
    - can be placed both inside and outside procedural code
    - subdivided into `observed` and `final`
- concurrent assertions
    - temporal
    - always clocked
    - inside and outside procedural code
    - use sampled values of their variables (value of the variable at the beginning of the simulation step)

Example:

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

### Assertion statements

- assertions: `assert`
- assumptions: `assume`
    - environment constraint
- cover statements: `cover`

## Language Design

### Assertion Statements

> SVA keywords:
> `assertion_keyword ::= assert | assume | cover | restrict`

> SVA syntax:
> `assertion ::= name : assert_keyword (assertion_body) action_block`

#### Kinds

```
+-----------+---------------------------------+-------------------+---------------------------------+
| Statement |               Kind              |     SVA Syntax    |              CHIPS              |
+===========+===========+=====================+===================+=================================+
|           |           |        simple       |            assert |  Assert.immediate : (id => ...) |
|           |           +----------+----------+-------------------+---------------------------------+
|           | immediate |          | observed |         assert #0 |   Assert.observed : (id => ...) |
|   assert  |           | deferred +----------+-------------------+---------------------------------+
|           |           |          |   final  |      assert final |    Assert.`final` : (id => ...) |
|           +-----------+----------+----------+-------------------+---------------------------------+
|           |            concurrent           |   assert property |   Assert.property : (id => ...) |
+-----------+-----------+---------------------+-------------------+---------------------------------+
|           |           |        simple       |            assume |  Assume.immediate : (id => ...) |
|           |           +----------+----------+-------------------+---------------------------------+
|           | immediate |          | observed |         assume #0 |   Assume.observed : (id => ...) |
|   assume  |           | deferred +----------+-------------------+---------------------------------+
|           |           |          |   final  |      assume final |    Assume.`final` : (id => ...) |
|           +-----------+----------+----------+-------------------+---------------------------------+
|           |            concurrent           |   assume property |   Assume.property : (id => ...) |
+-----------+-----------+---------------------+-------------------+---------------------------------+
|           |           |        simple       |             cover |   Cover.immediate : (id => ...) |
|           |           +----------+----------+-------------------+---------------------------------+
|           | immediate |          | observed |          cover #0 |    Cover.observed : (id => ...) |
|   cover   |           | deferred +----------+-------------------+---------------------------------+
|           |           |          |   final  |       cover final |     Cover.`final` : (id => ...) |
|           +-----------+----------+----------+-------------------+---------------------------------+
|           |            concurrent           |    cover property |    Cover.property : (id => ...) |
+-----------+---------------------------------+-------------------+---------------------------------+
| restrict  |            concurrent           | restrict property | Restrict.property : (id => ...) |
+-----------+---------------------------------+-------------------+---------------------------------+
```

### Property

A property defines its own language. A property is a **temporal** formula that can be either true or false on a given
trace. Therefore, properties are interpreted over traces.

#### Basic Property Operators

| Operator       | Associativity | Description |
| -------------- | ---------     | ----------- |
| `not`          |  -            | |
| `nexttime`     |  -            | |
| `and`          |  left         | |
| `or`           |  left         | |
| `until`        |  right        | |
| `until_with`   |  right        | |
| `always`       |  -            | |
| `s_eventually` |  -            | |

#### Boolean property operators

Property operators express Boolean connectives between properties:

| Operator | Signature | Description |
| -------- | --------- | ----------- |
| `not`    | `not :: prop => prop` | negation |
| `or`     | `or :: prop => prop => prop` | disjunction |
| `and`    | `and :: prop => prop => prop` | conjunction |
| `implies`| `implies :: prop => prop => prop` | implication |
| `iff`    | `iff :: prop => prop => prop` | equivalence |
| `if (b) p else q` | `ite :: bool => prop => prop => prop` | if conditional |
| `case (b) ...` |  `case :: bool => prop list => prop` | case conditional |

#### Unbounded Linear Temporal Operators (LTL)

| Operator | Signature | Description |
| -------- | --------- | ----------- |
| `until`  | `until :: prop => prop => prop` | unbounded weak |
| `until_with` | `until_with :: prop => prop => prop` | unbounded weak |
| `s_until` |  `s_until :: prop => prop => prop` | unbounded strong |
| `s_until_with` | `s_until_with :: prop => prop => prop` | unbounded strong |
| `always` | `always :: prop => prop` | unbounded weak |
| `s_eventually` | `s_eventually :: prop => prop` | unbounded strong |

#### Bounded Linear Temporal Operators (LTL)

Useful for specifying properties that must be satisfied within some specified range of clock ticks.

| Operator | Signature | Description |
| -------- | --------- | ----------- |
| `nexttime` | `nexttime :: prop => prop` | weak |
| `s_nexttime` | `s_nexttime :: prop => prop` | strong |
| `nexttime [m]` |  `nexttime :: nat => prop => prop` | weak |
| `s_nexttime [m]` | `s_nexttime :: nat => prop => prop` | strong |
| `eventually [m:n]` | `eventually :: (nat,nat) => prop => prop`| weak |
| `s_eventually [m:n]` | `s_eventally :: (nat, nat) => prop => prop` | strong |
| `s_eventually [m:$]` | `s_eventually :: (nat, inf) => prop => prop` | strong |
| `always [m:n]` | `always :: (nat, nat) => prop => prop` | weak |
| `s_always [m:n]` | `s_always :: (nat, nat) => prop => prop` | strong |
| `always [m:$]` | `always :: (nat, inf) => prop => prop` | weak |

### Sequence

A sequence is a rule defining a series of values in time. Like a property, a sequence defines a language, namely, the
set of words (traces) that match the sequence. Unlike the language of a property, the language of a sequence is
finitary. A sequence does not have a truth value, it has one initial point and zero or more match, or tight satisfaction
points. Like properties, sequences are clocked.

#### Sequence Operators

Sequence operators build new sequences from its operands (compositions).

| Operator | Signature | Description |
| -------- | --------- | ----------- |
| `[*...]` | `[*...] :: SExpr => SExpr => SExpr` |
| `[*]`    | `[*] :: SExpr => SExpr => SExpr`    |
| `[+]`    | `[+] :: SExpr => SExpr => SExpr`    |
| `##`     | `## :: SExpr => SExpr => SExpr` | sequence concatenation |
| `[->...]` | | goto repetition |
| `[=...]` | | nonconsecutive repetition |
| `throughout` | `throughout :: bool => seq => seq` | |
| `within` | `within :: seq => seq => seq` | sequence containment |
| `intersect` | `intersect :: seq => seq => seq` | sequence intersection |
| `and` | `and :: seq => seq => seq` | sequence conjunction | 
| `or` | `or :: seq => seq => seq` | sequence disjunction |
| `first_match` | `first_match :: seq => seq` | first match |

#### Suffix operators

| Operator | Signature | Description |
| -------- | --------- | ----------- |
| `\->`    | `\-> :: SExpr => PExpr => PExpr`  | overlapping suffix implication |
| `\=>`    | `\=> :: SExpr => PExpr => PExpr` | non-overlapping suffix implication |
| `#-#`    | `#-# :: SExpr => PExpr => PExpr` | overlapping followed-by operator (suffix conjunction) |
| `#=#`    | `#=# :: SExpr => PExpr => PExpr` | non-overlapping followed-by operator (suffix conjunction) |

### Resets

A reset condition is a condition upon which it is desired to stop evaluation of a concurrent assertion or sub-property
in a preemptive or abortive way.

- aysnc resets: `disable iff`, `accept_on`, `reject on`
- sync resets: `sync_accept_on`, `sync_reject_on`

#### Disable Clause

`disable iff`

#### Aborts

| Operator | Signature | Description |
| -------- | --------- | ----------- |
| `accept_on` | `accept_on :: AbortCondition => prop => prop` | async abort |
| `reject_on` | `accept_on :: AbortCondition => prop => prop` | async abort |
| `sync_accept_on` | `accept_on :: AbortCondition => prop => prop` | sync abort |
| `sync_reject_on` | `accept_on :: AbortCondition => prop => prop` | sync abort |

#### Examples

SVA:

```
a_disable: assert property (
  disable iff (reset)
  @(posedge clk) a |=> b
) else $error("FAIL")
```

### Operators

#### Sequence Methods

Sequence methods return a Boolean value. Syntax: `sequence_instance.method_name`

| Method | Signature | Description |
| ------ | --------- | ----------- |
| `triggered` |
| `matched` |

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
