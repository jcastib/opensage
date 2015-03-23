# S.A.G.E. Library #



# _app_ #

Basic S.A.G.E. Application Framework

The app library provides a framework for any SAGE executable. Its primary feature is a base class from which the specialized class will be derived. Besides that, there are a number of release-related functions available, providing information such as release version, application name, micro-version number, and so on.

The basic idea is that your SAGE program should be, regardless of the specific nature of the analysis, built from a derived version of SAGE::APP::SAGEapp. Then, in the main() function of your program, you can instantiate your SAGE::APP::SAGEapp derivation and invoke its SAGE::APP::SAGEapp::main() function.

```
#include "app/SAGEapp.h"

class myprogram : public SAGEapp
{
  public:
    myprogram(int argc=0, char **argv=NULL)
    { 
      // ... 
    }
   
    virtual int  main() 
    { 
      // ...
    }

    virtual void print_params(std::ostream &)
    { 
      // ... 
    }
    
    virtual void print_help(std::ostream &)
    { 
      // ...
    }
};

int main(int argc, char* argv[])
{
  myprogram prog(argc, argv);

  prog.main();

  return 0;
}
```

## Additional functionality ##

There are a number of components in the SAGE::APP library. Documentation on each major component follows...

### 1. Version numbering ###

Take a look at the class SAGE::APP::VersionNumber. You'll see that it's
designed to represent a version of SAGE.

There is one important thing to keep in mind for building the app library in this respect! The app library contains an instance of VersionNumber (called  'version'). This instance is constructed on the basis of four pre-compiler defines:

> SAGE\_MAIN\_VERSION
> SAGE\_SUB\_VERSION
> SAGE\_MICRO\_VERSION
> SAGE\_BETA\_VERSION

These symbols must be defined for the app library to build correctly. If any one of them is missing, the compile will fail (with an informative error message). Make sure that you define these symbols somewhere in your make system.

### 2. app\_names ###

Static class providing conversion functions for APP::app\_index\_type and micro version number

### 3. BasicParser ###

Virtual base class for all programs' parsers

### 4. LSFConvert ###

Converts LSF attribute data into basic data types

### 5. Output\_Streams ###

Provides basic output streams for SAGE programs

### 6. SAGEapp ###

Application framework for all SAGE programs



# _container_ #

S.A.G.E. Containers

The container library provides the various useful container classes for S.A.G.E. utilizing STL containers or boost libraries.  Many of them are templatized.


# _data_ #

Basic S.A.G.E. Data framework needed for most SAGE programs

The data library provides the command-line argument parser, data reading modules and a basic data storage for most SAGE programs. Its primary feature is a base class from which the specialized class will be derived. Besides that, there are a number of reading-in-data-related functions available.

The SAGE\_Data class is the basic data needed for most SAGE programs. Not all elements are needed for all programs, but the extra overhead in having them included here is made up for in the ease of maintainability.


# _error_ #

Error library

The error library defines errorstreams available to S.A.G.E. application programs.


# _fortran_ #

Module to support FORTRAN-formatted pedigree file

The fortran library provides the routine to read in FORTRAN-formatted user files.


# _fped_ #

Filtered Multipedigrees

Pedigree Structure Filtration Objects and Algorithms

## Introduction ##

Data provided to S.A.G.E. programs is often not immediately ready to be analyzed.  Our algorithms often must make assumptions about the types of data on theoretic grounds.  These assumptions come in the form of restriction criteria which must be met before the analysis can proceed.

The FPED Library is a tool for altering pedigree structure based upon such criteria.  Based upon an underlying SAGE::RPED::RefMultiPedigree, generally the source data, it can create arbitrary pedigree structures in which the members map back to a source member, in effect changing pedigree structures to suit algorithmic needs.  Some needs which are supported are:

```
- Removing uninformative members (ex. GENIBD, MLOD)
- Restructuring pedigree relationships (ex. RELTEST)
- Removing inbreeding, loops, marriage chains or clusters (ex. ASSOC)
- Splitting a subpedigree up into separate smaller subpedigrees (ex. GENIBD,
  MLOD)
```

And so on.  In these cases, the desired effect is to remove certain problematic or unneeded members, and/or to use different pedigree structures that are more amenable to the analysis.

Note that the FPED performs a very different function from other data cleaning objects.  It removes or modifies \em relationships within the pedigree structure.  It does not change member's data, though which members are altered may be chosen upon the basis of that member data.

Instead, each member of the FilteredMultipedigree can have a reference back to a member in the source multipedigree stored in its info class, which it uses to provide the same trait and phenotype information as the source member.  Members without such a reference are considered to have missing data at all traits and markers.  Similarly, each pedigree in the FilteredMultipedigree refers back to its source pedigree.

## Basic Structure ##

The FPED library breaks down into two pieces:

### 1. FilteredMultipedigree Objects ###

The FilteredMultipedigree Objects are a SAGE::MPED::multipedigree derived template instantiation and the associated info classes.  Information is stored at the Multipedigree, Pedigree and Member levels.  The last, which differs from the SAGE::RPED::RefMultiPedigree, is provided for direct access to member data through the member themselves.

The interfaces to the info classes mimic those provided by the
SAGE::RPED::RefMultiPedigree, with a few minor changes and additions.  The member info class also mimics the SAGE::RPED::RefPedInfo interface, but reduces it to specifically target the member.  Algorithms written to use the
SAGE::RPED::RefMultiPedigree should be totally compatible with the
FilteredMultipedigree, but often can be optimized with its use.  Both the Pedigree and Member level interfaces are restricted to const access only.

The FilteredMultipedigree class itself is a publicly-derived child of the MPED::multipedigree template.  The primary differences relate to
construction issues, as it is otherwise identical to a simple template
instantiation.

### 2. Multipedigree Filtration Objects ###

The Multipedigree Filtration Objects are provided to do basic filtering
based upon boolean functions on multipedigree elements, typically members.
The primary class is the MPFilterer, which copies (filters) members into a provided FilteredMultipedigree based upon a user-provided function.  It is assisted by a number of pre-defined functor objects which allow customization of the routines as needed for specific filtration tasks.  The MPFilterer assumes that lineage information is preserved whenever possible, and is removed whenever it would violate pedigree structure restrictions (ex.  If a parent is removed, the other parent lineage is removed from all children with the first parent, since a child must have both parents or neither).

Finally, a simple function, filter\_to\_sib\_pair(), is provided as an example of how filtered members need not have the same structural relationships as in the source multipedigree.  In this case, an arbitrary pair of members is created as a sib pair with dummy parents to facilitate RELTEST.s analysis.

## Constructing a FilteredMultipedigree ##

Creation of a filtered multipedigree is done in stages:

### 1. Creation ###

A FilteredMultipedigree is created based upon some source multipedigree.
This may be a RPED::RefMultiPedigree, or it may be another
FilteredMultipedigree. In either case, the source is passed through the
constructor and is used to test and verify that the FilteredMultipedigree is consistent with the underlying RPED::RefMultiPedigree (in the case of building based upon a FilteredMultipedigree, the FilteredMultipedigree automatically builds based upon the RPED::RefMultiPedigree underlying that
FilteredMultipedigree).

Example code:

```

RPED::RefMultiPedigree rp;
.
.
.

FPED::FilteredMultipedigree f(rp);

```

### 2. Adding Member and Lineage Information ###

In general, adding members and lineage can be done through a set of helper funtions in the MPFilterer static class.  These functions
provide most of the necessary functionality to make adding a pedigree,
subpedigree, or family to an existing FilteredMultipedigree based upon some filtration criteria.  See \ref FPEDFilterObjects for more details.

Example code:

```
 
// f is (some) functor that takes a multipedigree element (in this case an RPED::RefMember)
// and returns true or false
filter_functor<RPED::RefMember> filter;

FPED::MPFilterer::add_multipedigree_filtered_by_members(f, rp, filter);

```

Creating a custom FilteredMultipedigree is also fairly simple.  Each member which is added has its own info class, which is a reference back to the source RPED::RefMember.  These can be added when the add\_member() function is called, or later modified to point to different members.  Note that they should not be modified after final construction of the FilteredMultipedigree, which uses the source information in setting up the pedigrees.
If an member is not given an info class, they will be considered
a new, uninformative member that was not in the source RPED::RefMultiPedigree.  See the filter\_to\_sib\_pair() function for a simple example of this in production code.

Example code:

```

// member came from somewhere...
const RPED::RefMember& member;  

.
. // Do stuff
.
 
// Add the member
f.add_member(member.pedigree().name(), member.name(), member.gender(), FilteredMemberInfo(member));

// Add a new member that doesn't refer to anyone, but is in the same pedigree as
// the previous member
f.add_member(member.pedigree().name(), "dummy", MPED::male);

// Set this new dummy individual to have the information from the member as well.
filtered_member& fmember = f.member_find(member.pedigree().name(), member.name());

fmember.info() = FilteredMemberInfo(member);

```

### 3. Do a final construction of the FilteredMultipedigree ###

After members and their lineages are added, the filtered multipedigree must be built using the construct() function.  This calls the multipedigree build() function, but also constructs the FilteredPedigreeInfo objects.  It verifies consistency of the information as well, calling an internal error if there's a problem.

Example code:

```

.
. // Do lots of building
.
 
// finished building, construct the FilteredMultipedigree
f.construct()

// f is ready for use.

```

## Using the FilteredMultipedigree ##

Using the filtered multipedigree should be identical to using any other
multipedigree-derived type.  It should be a drop in replacement for the
RPED::RefMultiPedigree anywhere the access to the info is const, but may be optimized in a few ways.  First and foremost, algorithms which use the FilteredMultipedigree can do less error checking, as there is some assurance that the pedigree structures provided are, in fact, analyzable.  In addition, since members know their own data (through their info class), code complexity can be reduced in many places and code readability enhanced.



# _func_ #

Function

Creation of new variables from phenotypes and covariates in the user
supplied data

## Introduction ##

In addition to expressions that can be expressed in the Python programming
language, func supplies special functions which can be applied to markers.
There are also funtions for standardizing and windsorizing user supplied
variables.  See S.A.G.E. User Document for more details.

## How it works ##

The classes in the func library allow users of S.A.G.E. applications to
create arbitrary functions of the input data that they supply in the
pedigree data file by specifying expressions in the parameter file.  The func library classes use the Python language interpreter to parse these expressions.  They compare the variable names in the data structure resulting from the parsing with variable names in the pedigree data file.
Where a matches are found, values of the variables are supplied to the Python environment for each record in the data file.  Python is invoked to evaluate the expression and the result is used to populate the new variable in RefPedinfo objects.

## Using the func classes ##

Any application which uses a class that inherits from SAGE\_Data to process user supplied data will have the "func" capability.



# _functors_ #

Creation of "hard-coded" algorithms dynamically at runtime

## Introduction ##

The functors library lets you create "hard-coded" algorithms dynamically at runtime. More specifically, it provides a series of classes that encapsulate flow-control logic.

Ok, so what does that mean? The concept is a little tricky at first, but it's actually very useful and can both speed up your programs and make them far more robust.

Consider the following flowchart for building a chair:

  1. Get wood

> 2. Get nails

> 3. Are we in the US?

> YES Contact EPA to get approval for design.

> NO Contact UN to form a chair subcommittee.

> 4. Assemble chair.

Let's say you've got a program that implements the above procedure. In addition, let's say the program has to "build a chair" ten thousand times when it executes. Furthermore, let's say that when the program starts, the user tells it whether or not we're in the US (the condition for step #3).

Assuming the program has implemented the above procedure in a straight-forward manner, then there's a problem: Every time the procedure is evaluated, step #3 needlessly rechecks whether or not we're in the US. Since being-in-the-US is established once and for all when the program first runs, there's no need to keep checking it every time you run the procedure. If the program carries out the procedure ten thousand times in a single run, that's  9,999 too many evaluations of the condition in step #3.

Here's an example of how this flaw might show up in your program:

```
   
int main()
{
  bool in_the_us = areWeInTheUs();

  for(size_t i = 0; i < 10000; ++i)
  {
    getWood();
    getNails();

    if(in_the_us) // Ack! The condition never changes within the loop; this is very inefficient.   
    {
      contactEPA();
    }
    else
    {
      contactUN();
    }

    assembleChair();
  }
}

```

A common solution to this problem is to use function pointers. In the above case, you could easily write a function pointer to handle the conditional aspect of the loop.

In more complicated instances, however, it's useful to have a more comprehensive framework for dynamically assembling flow-controlled algorithms. That's what the functors library is for. It provides all the basic flow-control structures (if-then-else, do-while, for, etc.) plus some commonly-used non-standard structures (selection,  multi-selection, sequences).
You can construct, at runtime, complex flow-control algorithms that eliminate the negative overhead of needless re-evaluation of static conditions. In addition, by constructing your algorithms dynamically as a series of distinct functional steps, you can better insert and remove steps from the overall sequence. To tempt you, here's an example of how the above could be written using the functors library:

```

#include "functors/functors.h"

void getWood       () { /* ... */ }
void getNails      () { /* ... */ }
void contactEPA    () { /* ... */ }
void contactUN     () { /* ... */ }
void assembleChair () { /* ... */ }

int main()
{
  bool in_the_us = areWeInTheUs();

  SAGE::FUNCTORS::Sequence<void> s;

  s.addStep(boost::bind(&getWood, "get wood");
  s.addStep(boost::bind(&getNails, "get nails");
  s.addStep(boost::bind(in_the_us ? &contactEPA : &contactUN), "contact someone");
  s.addStep(boost::bind(&assembleChair), "assemble chair");

  for(size_t i = 0; i < 10000; ++i)
    s();
}
 
```

## Functors: a review ##

A "functor" is well-known concept in C++. A functor is defined as any object (class or struct, that is) with a publicly-accesible const-access operator() following a given signature. Furthermore, a functor must be default-constructable, copy-constructable, and assignable.
For instance, let's say you want a function to take in an int and return a string. According to this requirement, an acceptable functor would be any object with an operator() following the signature: std::string operator() (int) const. Also, the object must be default-constructable, copy-constructable, and assignable. For instance, all of the following classes are acceptable:

```

class foo { public: std::string operator() (int x) const { return "hi!"; } };

struct bar { public: std::string operator() (int y) const { std::ostringstream s; s << y; return s.

```

To review: Given a return type and function arguments, a functor is any object that (1) is default-constructable, (2) is copy-constructable, (3) is assignable, and (4) has a publicly accessible operator() following the signature: return\_type operator() (argument list) const.

## Boost::bind and functors ##

"But wait!" you exclaim. "I've got a class with a function that takes an 'int' and returns a 'string', but the function isn't operator(). It's called goNuts(). What do I do?"

This is where boost steps in to help up. With the boost::bind() function, you can create functors out of both member functions and non-member functions. The functors you create out of member functions can be bound to references or class instances themselves.

Here's how it works: You invoke boost::bind and pass it the information
required to run the function. boost::bind returns a functor instance that obeys the correct functor requirements. Consider the following class that you want to turn into an acceptable functor for the above example:

```
class foo { public: string goNuts(int x) const { return "nuts!"; } };
```

To "functorize" it, use boost::bind as follows:

```
boost::bind(&foo::goNuts, foo(), _1)
```

The first argument ("&foo::goNuts") is a member function pointer. The second argument ("foo()"), is a foo instance that will be copied into the functor that boost::bind returns. The third argument ("_1") tells boost to "pass on" a single argument given to the bound functor to the underlying goNuts function. This_1 corresponds to the 'int' argument that is passed to goNuts. If the function signature required more arguments, your invocation of boost::bind would follow the form "_1,_2, ..., _X". For instance, if the functor signature requires an int return type and an int, a char, and a string as function arguments, the following code would work:_

```
class foo { public: int doSomething(int, char, std::string) const { /* ... */ } };

int main() { boost::bind(&foo::doSomething, foo(), _1, _2, _3));
```

In the above example, the foo instance is actually copied into the functor.
If you want the member function to be bound to a reference rather than an
instance, use the boost::ref() function as follows:

```
class foo { public: int doSomething(int, char, std::string) const { /* ... */ } };

int main()
{ 
  foo f;
  boost::bind(&foo::doSomething, boost::ref(f), _1, _2, _3));
}
```

The boost::ref function ensures that f will not be copied into the functor; when doSomething is invoked, it will be on the f instance.

## General principles for classes in the functors library ##

### 1. Two purposes ###

For the most part, the major classes of this library (Reporter, Sequence, Switch, MultiSwitch, If, etc.) do two things. First, they
organize one or more functors in a logical way. The If class, for instance, organizes three functors: an 'if' functor that returns true or false, a 'then' functor which is executed if the condition if true, and an 'else' functor which is executed if the condition is false.

Second, these main classes are usable themselves \b as functors. They can be plugged into each other to create complex flow control structures.

### 2. Template arguments ###

Most of the main classes are templatized AT LEAST on two important
arguments: R and ARGS. R is the result type (or return type, if you will) of the class (or its constituent functors). ARGS is a boost::mpl::vector listing the arguments that will be passed to the functor / class. Remember how functors are defined in terms of their function arguments and return types? It is through the R and ARGS template parameters that this information is specified.

### 3. Typedefs and such ###

All of the main classes have the following typedefs:

result\_type - The result type of the functor (most of the time equivalent to the 'R' template parameter).

args - A boost::mpl::vector listing the input arguments (most of the time equivalent to the 'ARGS' template parameter).

arg[I](I.md)_type - The type of the I'th argument, where X is between 1 and 9. If X > length of the argument list, arg[X](X.md)_type evaluates to NullType.

In addition, the main classes have a static const int called 'arity' that indicates the number of arguments the functor requires.

## A more complex example ##

Consider the following flow chart:

(Given HEIGHT and WEIGHT):

1. Is HEIGHT > 10 ?

1.1. YES: Is WEIGHT > 100 ?

1.1.1. YES: Print "go crazy"

1.1.2. NO: Print "eliminate earth"

1.2. NO: Is WEIGHT > 150 ?

1.2.1. YES: Exit with status 0.

1.2.2. NO: Print "eliminate earth"

The above flowchart can be decomposed into three distinct 'if' statements (HEIGHT > 10, WEIGHT > 100, WEIGHT > 150) and three distinct 'action' statements (Print "go crazy", Print "eliminate earth", Exit with status 0). With this in mind, let's see how we can turn it into functor-friendly code:

```

struct heightAbove10 { bool go(int height, int weight) const { return height > 10; } };

struct weightAbove100 { bool go(int height, int weight) const { return weight > 100; } };

struct weightAbove150 { bool go(int height, int weight) const { return weight > 150; } };

struct printer
{ 
  void goCrazy(int height, int weight) const { std::cout << "go crazy"; }

  void eliminateEarth(int height, int weight) const { std::cout << "eliminate earth"; }
};
  
void exit0(int height, int weight) { exit(0); }

int main()
{
  // Typedef the conditional type to make things more readable:

  typedef SAGE::FUNCTORS::If<void, boost::mpl::vector<int, int> > ctype;

  // Put together steps 1.1, 1.1.1, and 1.1.2:

  ctype c1(boost::bind(&weightAbove100::go,      weightAbove100(), _1, _2),  // Step 1.1
           boost::bind(&printer::goCrazy,        printer(),        _1, _2),  // Step 1.1.1
           boost::bind(&printer::eliminateEarth, printer(),        _1, _2)); // Step 1.1.2

  // Put together steps 1.2, 1.2.1, and 1.2.2:

  ctype c2(boost::bind(&weightAbove150::go,      weightAbove150(), _1, _2),  // Step 1.2
           boost::bind(&exit0,                                     _1, _2),  // Step 1.2.1
           boost::bind(&printer::eliminateEarth, printer(),        _1, _2)); // Step 1.2.2

  // Put it all together:

  ctype c3(boost::bind(&heightAbove10::go, heightAbove10(), _1, _2, // Step 1
           c1,                                                      // c1 has encapsulated steps 1.
           c2);                                                     // c2 has encapsulated steps 1.

  // Now try running the algorithm with different height / weight combinations:

  c3(5, 10);

  c3(150, 0);

  c3(1000, 1000);

  c3(0, 0);

  return 0;
}
 
```

## Standard flow control classes ##

### 1. If ###
A SAGE::FUNCTORS::If encapsulates if-then-else logic in a single
functor. Each of the three steps (if, then, and else) are assigned as
contituent functors stored in the If instance. When invoked, a If instance first invokes in turn its "if" functor; then the If invokes its "then" or "else" functor (depending on the result of the "if" step).

### 2. Switch ###

A SAGE::FUNCTORS::Switch encapsulates the logic of a switch statement.
It takes an input object, classifies it according to some classification scheme, and selects an action on the basis of that
classification. The classification scheme for a Switch is a mutually-exclusive one, which is to say that an object can have one and only
one classification.

### 3. While and DoWhile ###

A SAGE::FUNCTORS::While encapsulates the while(condition) do { } logic. It stores two functors: an exit condition and a body.

A SAGE::FUNCTORS::DoWhile encapsulates the do { } while(condition) logic. It stores two functors: an exit condition and a body.

### 4. For ###

A SAGE::FUNCTORS::For encapsulates the for(...) { } logic. It stores four functors: an initializer, an exit condition, an index iterator, and a body.

## Non-standard flow control classes ##

### 1. Sequence ###

A SAGE::FUNCTORS::Sequence encapsulates a sequence of functors. Each functor represents a logical step in a process. When invoked, a Sequence iterates  through its functors and executes each one. The Sequence evaluates the result of each step before proceeding to the next one. It can, on the basis of the evaluation, either abort the sequence entirely or continue to the next step.

### 2. MultiSwitch ###

A SAGE::FUNCTORS::MultiSwitch takes an input object, identifies all of the classification categories to which it belongs and selects zero or more actions to take on the basis of those categories. The classification scheme for a MultiSwitch is NOT mutually exclusive, which is to say that an object can have zero or more classifications.

## Misc: Reporter, ThrowException, DoNothing, ReturnConstValue ##

There are a few miscellaneous "helper" functors available for your use.
These include:

A SAGE::FUNCTORS::Reporter is pretty simple: It "wraps" a functor; when the reporter is invoked (with the same signature as the "wrapped" functor) it does four things: (1) report that the functor has been called, (2) invoke the "wrapped" functor with the given arguments, (3) report that the functor has exited, and (4) returns the value returned by the "wrapped" functor.

SAGE::FUNCTORS::ThrowException - Throws an exceptions whenever it is
invoked.

SAGE::FUNCTORS::DoNothing - Does nothing whenever it is invoked. Only works for functors requiring a void result type.

SAGE::FUNCTORS::ReturnConstValue - Returns a given constant value whenver invoked.

## Special syntax ##

It is possible to use C++-like syntax to create the flow-control classes (SAGE::FUNCTORS::If, SAGE::FUNCTORS::While, SAGE::FUNCTORS::DoWhile, and SAGE::FUNCTORS::For).
The syntax works as follows:

### 1. 'If' statements ###

SAGE::FUNCTORS::If<...> (condition) [then\_action ](.md) .else_[else\_action ](.md)_

where 'condition' is the predicate functor and 'then\_action' and 'else\_action' are action functors.

### 2. 'While' and 'DoWhile' statements ###

SAGE::FUNCTORS::While<...> (condition) [body\_action ](.md)


# _gelim_ #

Genotype Elimination

Routine for genotype elimination.

# _globals_ #

S.A.G.E. Global Configuration Options

The globals module is primarily for global options used to configure
S.A.G.E. for various platforms, mostly for numerical compatibility, though it also includes typedefs that are of general use that aren't provided automatically on all platforms.

# _ibd_ #

Identity-by-Descent

Library for IBD read, store, & write.


# _LSF_ #

Logical Structured Format

The LSF library defines the structure of S.A.G.E. parameter file.

Things contained in {}s are optional.  ()**means zero or more of things
contained in the ().  ()+ means at least one.  | indicates or.  A \
preceding a ( or ) indicates that parenthesis is a character, not part of the expression.**

The following is a very left non-recursive CFG in EBNF for LSF variables.  It can be converted into LALR or LL in this form, and with the addition of semantic information most ambiguities disappear in actual usage.  The only potential problem is a shift/reduce conflict due to the left associativity of ']' in a LR parser scheme.  Also, it is assumed that the syntactic pre-processing has correctly tokenized the input.  A more complete CFG would include lexical parsing information as well.

VARIABLE ::= VAREXP ([VARIABLE](VARIABLE.md))**{.VAREXP}
> | $VARIABLE**

// Protected variable expression (protected by order of operation)
VAREXP   ::= $\(VARIABLE\)
> | IDENTIFIER

IDENTIFIER = ALPHANUM(ALPHANUM | '-' | '**')

ALPHANUM = alphanumeric character.**

Variables evaluate to AttrVals.  Where used as part of a subexpression,
they are used as the appropriate attribute type (string or integer).

Currently as implemented, identifiers are case sensitive for use as a
base.  Attribute names are not case sensitive.


# _lvec_ #

Lander-Green Likelihood Vector Library

The Library for Lander-Green likelihood calculation.


# _maxfun_ #

Function Maximization

MAXFUN is a module for maximization of an externally-defined function of an arbitrary number of variables under arbitrary externally-defined constraints. Because MAXFUN has been designed particularly with maximum likelihood estimation in mind, the variables are called parameters.

Given initial estimates and bounds on the parameters of the function, and given subroutines defining the function and constraints on acceptable parameter combination, MAXFUN seeks the parameter estimates that maximize the function value under those constraints.  because no single method of numerical optimization is best for all situations, MAXFUN allows for the use of six different methods: two direct search methods, two Newton-Raphson methods, and two variable metric methods.  The direct search methods are the most likely reach a local maximum within the specified bounds and constraints, but they tend to be inefficient in terms of computer time required.  The variable metric methods, in those cases in which they reach a local maximum, are usually the most efficient.  MAXFUN requires control values to be specified for the optimizing procedure, and the choice of these values also affects how well and how quickly a local maximum is reached.  The default for these control values have been found empirically to lead to good results in many cases.

MAXFUN returns to the calling program the best parameterestimates it finds and the corresponding function value, as well as flags and other information describing the conditions under which the procedure terminates.  It also provides estimates of the gradient at the final parameter values and, optionally, quantities that, if the function maximized is the natural logarithm of a likelihood, estimate the standard deviations of the parameters.


# _maxfunapi_ #

Maximization C++ API using MAXFUN =

## What is the Maxfun API? ##

The Maxfun API is a set of wrapper classes that provide an easy-to-use interface for Maxfun. You can still use Maxfun directly, but the wrapper classes make it easier to organize your model/parameters, execute a maximization, and process maximization results.

## How can I get started using it? ##

### 1. Start with a program that is ready to use Maxfun. ###

It's best to start with a program that already has the basic components for using maxfun. That means you should have an inherited instance of SAGE::MaxFunction, with the evaluate() and update\_bounds() virtual functions defined.

The MaxfunAPI features are then made available by including "maxfunapi.h" in your program. This file is located in the maxfun include directory (src/c++/include/maxfunapi).

Thus, in any file that will make use of the Maxfun API, you should include "maxfunapi/maxfunapi.h".

```
//=============================================
//
// File: MyFile.h
//
//=============================================
#include "maxfunapi/maxfunapi.h"
```

### 2. Instantiate ParameterMgr ###

Detailed explanation available at: SAGE::MAXFUN::ParameterMgr

SAGE::MAXFUN::ParameterMgr is a manager object that keeps track of your parameters. You will use it to set up the initial state for all your parameters, as well as fetch/set parameter values during maximization. You will need to create an instance of the SAGE::MAXFUN::ParameterMgr object somewhere in your program kernel. Generally, if you have some sort of "model" class that describes the particular details/options for your analysis, you should put the ParameterMgr object there.

```
using namespace SAGE;

class MyModel
{
        public:
                // ...

        private:
                // ...
                MAXFUN::ParameterMgr my_mgr;
};
```

You will also need some way to pull out a non-const reference to your ParameterMgr object. This is necessary because when you invoke SAGE::MAXFUN::maximize() (the API function for maximizing), you will have to pass it a non-const reference to the SAGE::MAXFUN::ParameterMgr.

```
using namespace SAGE;

class MyModel
{
        private:
                MAXFUN::ParameterMgr my_mgr;
        public:
                MAXFUN::ParameterMgr & getMgr() { return my_mgr; }
};
```

### 3. Setup ParameterMgr ###

Detailed explanation available at: SAGE::MAXFUN::ParameterMgr, SAGE::MAXFUN::Parameter, SAGE::MAXFUN::Parameter::ParamTypeEnum.

Set up your SAGE::MAXFUN::ParameterMgr object with the appropriate parameters.

There are a number of options available for each parameter (see SAGE::MAXFUN::Parameter), but for simplicity's sake we will only consider the six required components for the moment. These are:

Group name: Each parameter MUST belong to a named group. Even if your parameters do not need to be grouped, they must be placed in a named group within the Maxfun API. If they are "global" parameters, put each one in a group whose name is identical to the parameter. When you use the output formatting features later on, those functions will recognize that group name and parameter name are identical, and format the output as if that parameter was not in a group.

Parameter name: Each parameter must have a name. It's generally not a good idea to repeat names within a single maximization (for instance, to have two parameters named "Intercept", one in "Mean group" and one in "Variance group"), but it is possible. Generally speaking, you should pick a name that is intuitive and easy to read. (Note: There is also a SAGE::MAXFUN::Parameter::setNameAbbr() feature, which allows you to specify an alternate abbreviated name for output purposes. If there is not enough room to print the entire name in the output, then SAGE::MAXFUN::Parameter::getNameAbbr() will be used instead).

Initial type: There are four initial types available. SAGE::MAXFUN::Parameter::INDEPENDENT indicates a completely
independently estimated parameter. SAGE::MAXFUN::Parameter::INDEPENDENT\_FUNCTIONAL indicates an independently estimated parameter that is used in the calculation of a dependent parameter. SAGE::MAXFUN::Parameter::DEPENDENT
indicates a parameter that is dependent on other SAGE::MAXFUN::Parameter::INDEPENDENT\_FUNCTIONAL parameters, and whose
value you (the programmer) will calculate in each update\_bounds() call. SAGE::MAXFUN::Parameter::FIXED indicates a
parameter whose value will be fixed during the entire maximization. Please consult SAGE::MAXFUN::Parameter::ParamTypeEnum for more information on this option.

Initial estimate: The initial value for this parameter at the very beginning of maximization.

Lower bound: The (inclusive) lower bound for this parameter's estimation.

Upper bound: The (inclusive) upper bound for this parameter's estimation.

Please note that for upper and lower bound, SAGE::MAXFUN::MF\_INFINITY is predefined and available for use. It is used in the examples below.

You can add parameters to ParameterMgr with the SAGE::MAXFUN::ParameterMgr::addParameter() function. For instance, to
add a parameter in group "means" named "theta" with initial value = 1.0, lower bound = -Infinity, upper bound = +Infinity, and type = SAGE::MAXFUN::Parameter::INDEPENDENT, you would use the following code:

```
my_mgr.addParameter("means", "theta", MAXFUN::Parameter::INDEPENDENT, 1.0, -MAXFUN::MF_INFINITY, MAXFUN::MF_INFINITY);
```

You should add the SAGE::MAXFUN::ParameterMgr::addParameter() function calls wherever your kernel is set up:

```
void MyKernel::SetupParameters()
{
        // ...
        my_mgr.addParameter("means", "theta1", MAXFUN::Parameter::INDEPENDENT, 1.0, -MAXFUN::MF_INFINITY, MAXFUN::MF_INFINITY);
        my_mgr.addParameter("means", "theta2", MAXFUN::Parameter::INDEPENDENT, 1.0, -MAXFUN::MF_INFINITY, MAXFUN::MF_INFINITY);
        // ...
}
```

### 4. Modify your kernel ###

Detailed explanation available at: SAGE::MAXFUN::ParameterMgr

Modify your kernel to use the SAGE::MAXFUN::ParameterMgr object when it needs parameter values. Whenever you make  reference to some parameter value that you have explicitly implemented, you should instead change it to pull out the value from ParameterMgr.

For instance, let's say your original likelihood function uses a parameter called "my\_t" and looks something like this:

```
double MyKernel::Setup();
{
  my_t = 0.0;
}
 
double MyKernel::calculateLikelihood()
{
  return exp(-my_t * my_t);
}
```

Your modified code should look like this:

```

void MyKernel::Setup()
{
  my_mgr.addParameter("globalgroup", "t", MAXFUN::Parameter::INDEPENDENT, 0.0, -MAXFUN::MF_INFINITY, MAXFUN::MF_INFINITY);
} 

double MyKernel::CalculateLikelihood()
{
  return exp(-minfo("globalgroup", "t") * minfo("globalgroup", "t"));
}

```

Also, please note that the pulling out the parameter value by group name & parameter name is not the only way. You can also pull out parameter values by using their unique index number. This is advantageous because it avoids doing a name lookup, and hence runs a lot faster.

The unique ID number is returned by the original SAGE::MAXFUN::ParameterMgr::addParameter() function. You can store
that ID number, and then use it later in your kernel to more quickly fetch parameter values.

```

class MyKernel : public MaxFunction
{
  // ...

  private:
        my_t_idx;

  // ...
};
  
void MyKernel::Setup()
{
  my_t_idx = my_mgr.addParameter("globalgroup", "t", MAXFUN::Parameter::INDEPENDENT, 0.0, -MAXFUN::MF_INFINITY, MAXFUN::MF_INFINITY);
}
 
double MyKernel::calculateLikelihood()
{
  return exp(-minfo(my_t_idx) * minfo(my_t_idx));
}

```

### 5. Instantiate MaxFunction ###

Create an instance of MaxFunction, and customize your update\_bounds() and evaluate() functions.

```
class MyFunction : public MaxFunction
{
    virtual double evaluate      (vector<double> & params);
    virtual int    update_bounds (vector<double> & params);

    MyKernel my_kernel;
};
  
double MyFunction::evaluate(vector<double> & params)
{
  return my_kernel.calculateLikelihood();
}
 
int MyFunction::update_bounds(vector<double> & params)
{
  // ...
}
 
MyFunction my_func;

```

### 6. Maximize! ###

Detailed explanation available at: SAGE::MmximizeDefault(), SAGE::maximize(), SAGE::MAXFUN::Results,
SAGE::MAXFUN::DebugCfg, SAGE::MAXFUN::SequenceCfg

For a default maximization, you can invoke the function SAGE::MAXFUN::maximizeDefault(), and store the results in a SAGE::MAXFUN::Results object:

```
MAXFUN::Results my_results = MAXFUN::maximizeDefault(my_func.getParameterMgr(), my_func);
```

Alternately, you can invoke SAGE::MAXFUN::maximize(), which also takes a MaxfunDebug object and a MaxfunConfig object as additional parameters. You will have to create instances of those two objects before invoking SAGE::MAXFUN::maximize().

```
MAXFUN::DebugCfg my_dbg(MAXFUN::DebugCfg::COMPLETE);
MAXFUN::SequenceCfg my_cfg(MAXFUN::SequenceCfg::DEFAULT_MAXIMIZATION);
MAXFUN::Results my_results = MAXFUN::maximize(my_func.getParameterMgr(), my_func, my_cfg, my_dbg);
```

The Results object contains a great amount of information about the maximization, including final parameter estimates, standard errors, derivatives, p-values, maximization status, variance-covariance matrices, and more.

#### 6.1 Debugging configuration ####

Although you can directly set up your SAGE::MAXFUN::DebugCfg instance
however you want it, you may want to set up your SAGE program to read the debug configuration on a per-analysis basis from the parameter file
associated with your analysis. To do so, you'll want to make sure that there is an accessible SAGE::MAXFUN::DebugCfg instance available in your model.   Then, when you're parsing the parameter file, if you encounter a "MAXFUN"  sub-block you can invoke SAGE::MAXFUN::parseDebugParams() to setup the debugging object. Later on, when you invoke your analysis, your debugging object will already be configured for runtime output.

### 7. Generate output ###

Detailed explanation available at: SAGE::MAXFUN::formatEstimates(), SAGE::MAXFUN::formatMatrix(),
SAGE::MAXFUN::formatJointTest()

There are a number of global functions available for turning your maximization results into output. These functions return the output in the form of a std::string, which you can use any way you want. These functions include:

SAGE::MAXFUN::formatEstimates(): Provides a summary of maximization condition, as well as final parameter estimates, standard errors, p-values, derivatives, and final function value.

SAGE::MAXFUN::formatJointTest(): Performs a chi-square joint test between two Results objects.

SAGE::MAXFUN::formatMatrix(): Provides a variance-covariance matrix.

Let's say you want to generate all three output features (estimates, joint test, and matrix):

```
MAXFUN::Results my_results1 = MAXFUN::maximizeDefault(my_func1.getParameterMgr(), my_func1);
MAXFUN::Results my_results2 = MAXFUN::maximizeDefault(my_func2.getParameterMgr(), my_func2);

cout << MAXFUN::formatEstimates (my_results1);
cout << MAXFUN::formatMatrix    (my_results1);
cout << MAXFUN::formatJointTest (my_results1, my_results2);
```

### 8. A short example ###

Here is a complete example of how to use the Maxfun API:

```
#include "maxfunapi/maxfunapi.h"

using namespace std;
using namespace SAGE;

class MyFunction : public MaxFunction
{
public:
  MyFunction();

  virtual double evaluate      (parameter_vector& theta);
  virtual int    update_bounds (parameter_vector& theta);

  MAXFUN::ParameterMgr m;
};

MyFunction::MyFunction()
{
  m.addParameter("global", "X", MAXFUN::Parameter::INDEPENDENT, -1.0, -MAXFUN::MF_INFINITY, MAXFUN::MF_INFINITY);
}
 
double MyFunction::evaluate(parameter_vector& tr)
{
  double result = exp(-(m("global", "X") * m("global", "X")));

  return result;
}
 
int MyFunction::update_bounds(parameter_vector& tr)
{
  return 0;
}
 
} // End namespace

int main(int argc, char* argv[])
{
  MyFunction my_func;

  MAXFUN::Results results = MAXFUN::maximizeDefault(my_func.m, my_func);

  cout << MAXFUN::formatEstimates(results);

  return 0;
}
```

When executed, the above program will produce the following output:

```
================================================================================
          MAXIMIZATION RESULTS Default analysis
================================================================================

Parameter                      Estimate          S.E.        P-value         Deriv

global
                             X -0.000000         0.707107    1.000000        0.000000

Final function value: 1.000000
```

## More advanced runtime debugging ##

The first thing you should do to learn more about maxfunapi's debugging
features is to read the complete detailed description of
SAGE::MAXFUN::DebugCfg. The basic idea here is that the runtime debugging options are specified via an instance of SAGE::MAXFUN::DebugCfg.  Although the simple version of maximize() implicitly generates a SAGE::MAXFUN::DebugCfg instance with the
SAGE::MAXFUN::DebugCfg::NO\_DEBUG\_INFO template, you can specify your own optoins by creating your own SAGE::MAXFUN::DebugCfg object:

```
SAGE::MAXFUN::DebugCfg my_dbg;
```

Now you can set up whatever specific options you want for your runtime
debugging!

```
my_dbg.setReportBasicConfig(true);
SAGE::MAXFUN::maximize(my_func.getParameterMgr(), my_func, my_sequence_cfg, my_dbg);
```

## What about redirecting debugging output to a specific file or stream? ##

Redirecting runtime debugging output isn't too complicated. SAGE::MAXFUN::DebugCfg lets you specify which outputstream to use, via the SAGE::MAXFUN::DebugCfg::setDebugOutput() functions. To modify
your code to redirect debugging output to a file or outputstream, all you need to do is invoke SAGE::MXAFUN::DebugCfg::setDebugOutput() prior to invoking SAGE::MAXFUN::maximize():

```
SAGE::MAXFUN::DebugCfg my_dbg(SAGE::MAXFUN::DebugCfg::DEBUG_COMPLETE);
my_dbg.setDebugOutput("somefile.max", false);
SAGE::MAXFUN::maximize(my_param_mgr, my_max_function, my_sequence_cfg, my_dbg);
```

## I want to see a more detailed explanation. ##

Please consult the reference section of this manual for more detailed information. Each class has a detailed description that will explain much more about its functionality and use within the API.


# _mcmc_ #

Markiv Chain Monte Carlo Simulation Engine for Inheritance of Marker Alleles


# _mfsubmodels_ #

Submodels for use with Maxfun

## What is the mfsubmodels library? ##

The mfsubmodels library provides a variety of submodels for use with the Maxfun API. If you're not familiar with the Maxfun API already, you should read the tutorial for that library. This tutorial presupposes familiarity with maxfunapi.

## Remind me again how MAXFUN::Submodel's work! ##

MAXFUN::Submodel are handy little object for encapsulating related
parameters as well as functionality for processing them. For instance, let's say you want to estimate transformation parameters in your model? You could explicitly add power and shift parameters to your MAXFUN::ParameterMgr, and add some transformFoo() function to your kernel. That would work fine. But wouldn't it be nice if such a chunk of code already existed?

Well, it does!

The MFSUBMODELS::TransformationSubmodel does this for you. All you need to do is (1) instantiate it somewhere in your model, (2) remember to add it to your MAXFUN::ParameterMgr immediately prior to maximization, and (3) remember to remove it therefrom immediately after maximization.

## How do I parse the parameter file sub-block for any given submodel? ##

For every submodel FooSubmodel, there is a corresponding class named
FooParser. This class will have a publicly available static parsing
function, generally of the form

parse(submodel &, const LSFBase **param, cerrorstream & errors)**

For instance, if you want to parse a transformation sub-block, you can use SAGE::MFSUBMODELS::TransformationParser::parse() to do so.

## List of available submodels and their parsers ##

For transformation math:

SAGE::MFSUBMODELS::TransformationSubmodel, SAGE::MFSUBMODELS::TransformationParser

For type-specific (usually by genotype) collections of values:

SAGE::MFSUBMODELS::TypeSpecificSubmodel, SAGE::MFSUBMODELS::TypeSpecificParser


# _mlocus_ #

Marker Locus Library =

Library for marker locus data storage & manipulation.


# _mped_ #

Pedigree Structural Storage Classes =

## What is mped? ##

The mped.new library lets you organize pedigree data into convenient storage mechanisms. It organizes individuals within a hierarchical system, starting with the most specific (the individual itself) to the most general (the set of all individuals). The typography of organization is as follows:

o Member - The individual

o Family - A nuclear family, composed of two parents and at least one child

o Subpedigree - A set of individuals all related to each other either by marriage or by blood

o Pedigree - A set of individuals all sharing the same pedigree id (not necessarily related by marriage or blood)

o Multipedigree - The set of all pedigrees

Please note that mped uses the following definitions for offspring and progeny:

o offspring - given two parents, offspring refers to those two parents' children

o progeny - given a single parent, progeny refers to all that parent's children from all that parent's mates

It is important to note at this point why there is a distinction between pedigree and multipedigree. If the pedigree represents unrelated individuals, why is it necessary to distinguish between the multipedigree and pedigree?

The answer lies in the nature of field-collected data. Often, field-collected data will code individuals as being within the same set, even though they may not be related by marriage or blood. Since those individuals were collected within the confines of a discrete data collection effort, however, it may be fruitful to analyze them as if they did indeed share some common characteristics. A study, for instance, may have collected information from a number of caucasians; another study may have collected information from a number of asians. When the data are collated together, each set will retain a unique pedigree id code. Consequently, if the analysis calls for an examination of "familial" effect, the distinction between the races may be telling.

Please note: Within SAGE program development, you will generally not need to make extensive use of mped.new. Another library (rped) is a full-featured extension of mped, and as such is designed for direct use by programmers.

## Bases class vs. derived classes ##

As stated above, mped organizes pedigree data into five major storage units. There is also a second level of distinction in mped's form of storage: base classes (non-templatized) and derived classes (templatized).

### Base classes (non-templatized) ###

Base classes in mped are distinguish by the _base suffix (multipedigree\_base, pedigree\_base, subpedigree\_base, family\_base, and member\_base). The classes exist to track only pedigree structure. Pedigree data is treated in the derived classes (see next section)._

Why, you might ask, is mped designed to separate structure from trait data in its class organization? This is done so that any algorithm that treats only structure can be generalized. That is, assuming there are multiple derived/specialized forms of the base storage classes, any pedigree structure-based algorithm can be written once.
Many of the functions in mp\_utilities, for instance, are designed to process mped's base storage classes.

### Derived classes (templatized) ###

The derived storage classes (multipedigree, pedigree, subpedigree, family, and member) are designed to extend the their structure-based parent classes to include any kind of trait data. To accomplish this, the derived storage classes are templatized on an info class for each of the five organizational units. That is, a unique info type is associated with each of the five levels of data organization. This also the template specialization (see SAGE::RPED, for instance) to determine which levels of data organization require which types of associated data.

In one extreme case, let's consider a template specialization where you didn't want any new information associated with your derived storage classes. You could accomplish this by using the SAGE::MPED::no\_info struct in your own template specialization:

```

typedef SAGE::MPED::no_info no_info;
typedef SAGE::MPED::multipedigree<no_info, no_info, no_info, no_info, no_info> q_multipedigree;

q_multipedigree m();

```

Now, let's say you want to associate a status (valid vs. invalid) with every family in your template specialization.
You can do this by creating a class to track validity, then declaring your storage classes with that status class as a template parameter:

```

class StatusClass
{
public:
  bool validity;
};
  
typedef SAGE::MPED::multipedigree<no_info, StatusClass, no_info, no_info, no_info> q_multipedigree;

q_multipedigree m();

```

Once you have created a template specialization of an mped derived storage class, you can use that specialization to create other storage classes with the same template parameters. Let's say you want to extend the previous example by creating a pedigree\_iterator. Since the pedigree\_iterator is templatized, you can fetch the correctly templatized form of the pedigree\_iterator through the multipedigree specialization:

```

class StatusClass
{
public:
  bool validity;
};
  
typedef SAGE::MPED::multipedigree<no_info, StatusClass, no_info, no_info, no_info> q_multipedigree;

q_multipedigree m();

q_multipedigree::pedigree_iterator p;

```

## Iterators ##

There are iterators (const and non-const) available within mped for the following types:

o pedigree\_base, pedigree

o subpedigree\_base, subpedigree

o family\_base, family

o mate\_base, mate

o offspring\_base, offspring

o parent\_base, parent

o progeny\_base, progeny

o sibling\_base, sibling

These iterators are used to traverse the different sets of data present in the entire multipedigree.

## More info... ##

Please consult the modules section of this documentation for more detailed information. Also, remember that within each class there is a detailed description explaining more about how to use that class.


# _numerics_ #

Numerical Computation =

Place for the interface for external numerical libraries, lapack ..., and the definition of statistical functions.


# _output_ #

Output Organizing and Rendering Library =

Structures for organizing output data into sections, tables, histograms, etc.

## What is the Output library? ##

The output library is a collection of classes, where each class represents a document **component**. You can create document components, populate them with values, insert them into each other, and generate output files based on their content.

## Getting started ##

### 1. What are the different components? ###

All components are derived from SAGE::OUTPUT::Element. The basic function of SAGE::OUTPUT::Element is to (1) manage a list of child elements, and (2) provide for the getting/setting of element "attributes" (where "attribute" is a name/value pair). The types of child elements that are allowed to be placed **in** a particular Element derivation are determined at compile-time.
That is, if you try to insert a Table into a Double, for instance, the
compiler won't let you do it. The documentation for each element explains which child elements, if any, can be inserted into it.

The component list (classes derived from Element) include:

  * SAGE::OUTPUT::Double, SAGE::OUTPUT::Int, SAGE::OUTPUT::String - Representing simple types

  * SAGE::OUTPUT::NamedDouble, SAGE::OUTPUT::NamedInt, SAGE::OUTPUT::NamedString - Representing named simple types

  * SAGE::OUTPUT::Section - For organizing components into groupings

  * SAGE::OUTPUT::Table, SAGE::OUTPUT::TableColumn, SAGE::OUTPUT::TableRow - For representing a table

  * SAGE::OUTPUT::RenderingRules - For indicating specific rendering rules for a component

### 2. What do I need to include in my code to make the library available? ###

- Include "output/output.h"

- Make sure -loutput is on your LDLIBS line in your Makefile

### 3. A simple example ###

Here's a quick example of how to construct a document and generate an output
file:

```

#include "output/output.h" // Include the necessary file

using namespace SAGE::OUTPUT;

int main()
{
  Section s("Top section");        // Create a section

  s << (Table()                           // Create a Table
    <<   (TableRow() << 1.0 << "Foofoo")  // Add a TableRow
    <<   (TableRow() << -9 << -5 << -3)   // Add another TableRow
    <<   (Table::INSERT_BLANK_ROW()))     // Add a blank TableRow
    << NamedInt("Size", 4);               // Add a NamedInt

  std::cout << s;                       // Output the section's contents to std::cout
  s.generatePrettyPrintFile("foo.txt"); // Output the section's contents to 'foo.txt'
  s.generateXmlFile("foo.xml");         // Output the section's contents as XML to 'foo.xml'
}

```

Let's review the above example in a little more detail. First of all,
remember that you have to include 'output/output.h' to make the API
available.

Now, let's look at how the different components are created. You are
generally allowed to instantitate any particular component you want. Some constructors require titles to be passed as arguments (such as
TableColumn's); some do not. A Section, for instance, must have a title. An Int (a simple value) does not require a title.

After creating each component, that component is then added to a parent
component. At the beginning of main(), a Section is created that will store the entire document. A Double is inserted into it, as well as a String. Then a Table is created, populated with content, and added to the Section instance.

Lastly, the contents of the Section are generated as an output file
'foo.txt', as well as an XML-formatted file 'foo.xml'.

Although the selection of components may seem small at first, you will find (hopefully!) that the selection is in fact quite versatile. You should be able to create complete SAGE output files made up of basic values, named values, sections, and tables.

### 4. Compile-time errors ###

The output library is designed to favor compile-time errors over runtime errors. If you try to add a disallowed child component (a Table into a Double, for instance), your code won't compile. Most of the time you'll get an informative comment accompanying your compiler error. If not, look at the line number cited by the compiler; an informative message will accompany the given line of code in the file.

## Designing your own components ##

Writing your own new component isn't particularly difficult.

## Reference information ##

For each of the following objects, the list thereafter indicates which
objects can be inserted into them:

### Double ###

  * Allowable child elements: [None](None.md)
  * Required attribute:  VALUE (floating point) indicates numeric value.

### Int ###

  * Allowable child elements: [None](None.md)
  * Required attribute:  VALUE (integral) indicates numeric value.

### String ###

  * Allowable child elements: [None](None.md)
  * Required attribute:  VALUE (string) indicates string value.

### NamedDouble ###

  * Allowable child elements: [None](None.md)
  * Required attribute:  TITLE (string) indicates name of value.
  * Required attribute:  VALUE (floating point) indicates numeric value.

### NamedInt ###

  * Allowable child elements: [None](None.md)
  * Required attribute:  TITLE (string) indicates name of value.
  * Required attribute:  VALUE (integral) indicates numeric value.

### NamedString ###

  * Allowable child elements: [None](None.md)
  * Required attribute:  TITLE (string) - name of value.
  * Required attribute:  VALUE (string) - string value.

### Graph ###

  * Allowable child elements: [None](None.md)
  * Required attribute:  TITLE (string) - name of graph to generate
  * Required attribute:  TYPE (string - "LINE\_GRAPH", "SCATTER\_PLOT", "BAR\_CHART") - type of graph to generate
  * Required attribute:  X\_COLUMN (string) - Name of column that has x-coordinate values
  * Required attribute:  X\_TYPE (string - "NUMERICAL", "CATEGORICAL") - Type of values in the x-column
  * Required attrbiute:  Y\_COLUMN[x](x.md) (string, [x](x.md) = integer) - Name of column that has y-coordinate values

- RenderingRules [None](None.md)

- SpannedCell [None](None.md)

- UnavailableCell [None](None.md)

- TableRow [SpannedCell, UnavailableCell, String, Double, Int]

- TableColumn [RenderingRules](RenderingRules.md)

- Table [RenderingRules, TableColumn, TableRow, Graph]

- Section [Section, Table, String, NamedDouble, NamedInt, NamedString]


# _pairs_ #

Member Pairs =

Generation of member pairs of different relationship types

## Introduction ##
pairs contains two distinct classes for generating member pairs: pair_generator and RelationMatrix.  The pair\_generator provides access to a set number of relationship types via iterators.  The RelationMatrix
precalculates all relationships MultiPedigree members and provides access through the get\_relationship and get\_complete\_relationship member functions._

## pair\_generator::relative\_pair ##

Pairs generated by the pair\_generator are represented by a relative\_pair.  The relative pair types are: parental (parent-offspring), sibsib (sibling-sibling), sissis (sister-sister), brobro (brother-brother), brosis (brother-sister), grandp (grandparent-grandchild), avunc (uncle or aunt-niece or nephew), halfsib (half sibling-half sibling), and cousin (cousin-cousin).  The relative pair consists of pointers to the two members making up the pair and pointers to up to two connectors. Connectors are the people in the pedigree who define the relationship between the two pair members.  For instance, grandparent-grandchild pairs are connected by the
person who is the offspring of the grandparent and the parent of the
grandchild; half sibling-half sibling pairs are connected by their common parent, etc.

## Using pair\_generator ##

The pair generator requires a pointer to a RPED::RefPedigree and an unsigned int representing the combination of pair types to be generated.  These can be supplied via the constructor or with set functions. The unsigned integer representing the pair types may be constructed by bitwise "oring" members of the mask enum.  pair iterators of supplied via begin and end functions.


## RelationMatrix pair types ##

The RelationMatrix can yield RelationTypes or CompleteRelationTypes.  The RelationType provides the following information: distance1, distance2, bridge, phase1 and phase2.  The distances are the number of generations from the respective pair members to their common point.  Phases refer to whether the pair member's parent1 or parent2 are in the lineage to the pairs common point. Parent1 and Parent2 are designated in the pedigree data file.  Bridge type refers to the "jump" that is made in a shortened traversal of the pedigree from one
pair member to the other.

The CompleteRelationType provides lineage1 and lineage2 in addition to the RelationType.  The lineages are member lists linking the respective pair member to the common point.

## Using RelationMatrix ##

A RelationMatrix is constructed with a RefMultiPedigree pointer as an
argument. get\_relationship and get\_complete\_relationship functions may then be used to get relationship information between any two members of the RefMultiPedigree.


# _peeling_ #

Templatized interface and caching algorithms for subpedigree peeling.

## What is peeling? ##

Peeling is a procedure for calculating likelihoods based on pedigree data.  See Elston Stewart, 1971.  The peeling classes in this library are based on the peeling methon introduced in Fernando, Stricker and Elston, 1993.

## The peeling classes ##

There are two peeler classes: the one in peeler2.h is based on the
Multipedigree::subpedigree\_type.  The one in peeler3.h is based on the
FilteredMultipedigree::subpedigree\_type.

As interfaces (abstract classes), the peeler classes are not in themselves complete algorithms, but include a variety of functions which must be filled in.  They are templatized to allow multiple different algorithms to be computed.  Different algorithms require different data to be passed up and down anterior/posterior chains.  The peeler, and its helper classes are templatized upon a Data class, the data which is needed to compute or reference a particular set of anterior/posterior values, and a Result class, the data which is returned from an anterior/posterior calculation.

The public interface of the peelers consists of inline functions
for the caching (using the peeling\_cache from cache2.h or cache3.h) and lookup of previously computed results.  All actual computation is done using the private functions which must be filled in by the user.  This is accomplished by specializing the class based upon the Data and Result type and adding those functions.


## Caching ##

There are two individual\_cache classes in cache2.h and cache3.h.  They are completely analagous to the two peeler classes.  They declare access functions to be defined by the application programmer.  The access functions are categorized as follows:

The access functions can be grouped into three categories:

  * Test for existance - boolean test to see if there is a cached value for the data or not.
  * Cached result lookup - returns the cached result.
  * Cached result setting - Set the cached result.

There are 4 functions in each category:

  * Anterior           - The cached result for the part of the pedigree anterior to the individual
  * Posterior          - The cached result for the part of the pedigree posterior to the individual, including all mates.
  * Posterior w. mate  - The cached result for the part of the pedigree posterior to the individual, but only with a particular mate.
  * Posterior w/o mate - The cached result for the part of the pedigree posterior to the individual, but with all mates except a particular mate.


# _rped_ #

Referenced Multipedigrees

Pedigree data structures including individual traits, markers and covariates

## What is rped? ##

rped is an extension of the mped library. It extends mped by providing info classes (attached to each level of structural representation) that store trait/marker information. Also, it provides functionality for reading in pedigree data from a source file.

## mped: A brief review ##

mped provides basic storage classes for the structural representation of a pedigree (structural, in this sense, applies to lineage: parent-offspring relationships). In addition, mped allows a templatized info object to be attached to each level of structural representation (multipedigree, pedigree, subpedigree, family, and member). Keep in mind that although mped allows a templatized info object to be attached, mped does not in fact provide any info objects (this is accomplish through rped).

## rped's template specialization ##

At its core, rped's primary piece of functionality is its template
specialization of SAGE::MPED::multipedigree. This specialization
(SAGE::RPED::RefMultiPedigree) is a version of SAGE::MPED::multipedigree templatized on RefMPedInfo and RefPedInfo (see next section).

Furthermore, all the template-specific forms of the mped derived classes (SAGE::MPED::member\_const\_pointer, for instance) are available with the same name in the SAGE::RPED namespace. For instance, if you want to const-iterate across members, you could do the following:

```
for(RPED::member_const_pointer mem = /* ... */) { /* ... */ }
```

## The info classes ##

The above template instantiations use two classes from rped as info
objects: SAGE::RPED::RefMPedInfo (attached to SAGE::MPED::multipedigree), and SAGE::RPED::RefPedInfo (attached to SAGE::MPED::pedigree). The remaining three structural classes (SAGE::MPED::subpedigree, SAGE::MPED::family, and SAGE::MPED::member) have SAGE::MPED::no\_info classes attached as info classes. (That is, no additional information is attached to those structural levels).

The two info classes are designed to provide trait & marker data storage in rped. SAGE::RPED::RefMPedInfo (the multipedigree info object) stores meta information about traits and markers (trait names, types, missing value codes, etc.).  SAGE::RPED::RefPedInfo, conversely, stores actual trait/marker values.

## How to import pedigree data ##

Please note ! Before reading this section, please review the section of the user manual on pedigree data file specifications (section 2.3 of the SAGE 4.6 manual). You need to be very familiar with the structure of a pedigree file and its constituent members before you try to use SAGE to read in that data.

First, let's review the class hierarchy that ends with the two classes
SAGE::RPED::RefLSFFortranPedigreeFile and
SAGE::RPED::RefLSFDelimitedPedigreeFile. It's a little complicated, but with good reason.

At the top level is the class RefPedigreeFile, which stores data importation configuration options for reading in a pedigree file. Recall that pedigree can be formatted in one of two ways: character-delimited or fortran-formatted (see user manual for more information). The RefPedigreeFile, however, does not know about this delimited vs. fortran distinction. It stores configuration options general to both formats.

Accordingly, there are two classes derived from SAGE::RPED::RefPedigreeFile:
SAGE::RPED::RefFortranPedigreeFile and SAGE::RPED::RefDelimitedPedigreeFile.
These two classes store data importation configurations options specific to the pedigree format (delimited vs. fortran).

In addition, it should be noted that the RefPedigreeFile does not have any parameter file parsing ability. You can manually scan the PEDIGREE block of your parameter file and manually set the configuration options. Since this is very annoying and tedious, there is a class designed to do this for you:
SAGE::RPED::RefLSFPedigreeFile. SAGE::RPED::RefLSFPedigreeFile's interface is very simple: two functions for processing LSF parameters. It should be noted that since SAGE::RPED::RefLSFPedigreeFile needs to be able to set options directly in a SAGE::RPED::RefPedigreeFile, it is accordingly derived from that object.

Lastly, we come to the two classes you'll actually use directly:
SAGE::RPED::RefLSFFortranPedigreeFile and
SAGE::RPED::RefLSFDelimitedPedigreeFile. Each of these classes is multiply derived, from a storage class (SAGE::RPED::RefFortranPedigreeFile or SAGE::RPED::RefDelimitedPedigreeFile) and the LSF parsing class
(SAGE::RPED::RefLSFPedigreeFile). These classes combine LSF parsing
ability with pedigree file format-specific configuration options.

Let's say you want to use an LSF-formatted parameter file to determine the data importation options for your RefMultiPedigree. You'll need the
following items: an LSFBase pointer (pointing to the "PEDIGREE" block),
a SAGE::RPED::RefLSFPedigreeFile pointer (for processing this pedigree block), a SAGE::RPED::RefMultiPedigree, and the name of the pedigree data file.

```
std::string pedigree_filename = "somefile.ped";
SAGE::RPED::RefMultiPedigree ref_multi_pedigree;
SAGE::RPED::RefLSFPedigreeFile * pedigree_reader;
SAGE::LSFBase * params = /* Read in the appropriate parameter block here */ ;
```

Now, you should scan your pedigree block to see if it indicates a
character-delimited or fortran-formatted file. If the attribute list on the pedigree block itself has a "column" attribute, then this is a
fortran-formatted file. Otherwise it is character-delimited. Accordingly, create the correct LSF reader:

```
if(params->attrs() && params->attrs()->has_attr("column"))
{
  pedigree_reader = new SAGE::RPED::RefLSFFortranPedigreeFile();
}
else
{   
  pedigree_reader = new SAGE::RPED::RefLSFDelimitedPedigreeFile();
}
```

Next, you can invoke your pedigree\_reader's process\_parameters() function (see SAGE::RPED::RefLSFPedigreeFile::process\_parameters() ) to process the whole "PEDIGREE" block:

```
pedigree_reader->process_parameters(ref_multi_pedigree.info(), params);
```

Lastly, you can use your pedigree reader to use the configuration options (which you have just set) to import the actual pedigree data file (see SAGE::RPED::RefPedigreeFile::input() ) :

```
pedigree_reader->input(ref_multi_pedigree, pedigree_filename, ref_multi_pedigree.info());
```

## How to manually set up a trait ##

If the provided LSF parsing features are insufficient for any reason, you can still manually set up any trait you want in your RefMultiPedigree. There are two steps to this process:

  1. Add a trait entry to the multipedigree info object (SAGE::RPED::RefMultiPedigree::info() )

> 2. Add a trait entry for every constituent pedigree's info object (SAGE::RPED::RefPedigree::info() )

Recall that trait information is stored in two distinct locations: The
meta-information about the trait is stored in the SAGE::RPED::RefMPedInfo object (attached to the multipedigree), whereas trait values are stored in SAGE::RPED::RefPedInfo objects attached to each constituent pedigree.

For instance, if you want to add a continuous trait named "height", the
following code would be sufficient:

```
SAGE::RPED::RefMultiPedigree rmp;

// Add trait to RefMPedInfo...
int id = rmp.info().add_trait("height", SAGE::RPED::RefTraitInfo::continuous_trait, SAGE::RPED::RefTraitInfo::trait_covariate);

// Add trait to RefPedInfo's...
for(rmp::pedigree_iterator ped = mp.pedigree_begin(); ped != mp.pedigree_end(); ++ped)
{
   ped->info().resize_traits(ped->info().trait_count() + 1);
}   
```

## How to iterate across pedigrees / subpedigrees / families / members ##

Since rped only adds trait/marker information to individuals, iterating
across pedigree \b structure is identical to mped. For instance, if you
want to iterate across all members in the RefMultiPedigree, you could do the following:

```
// Assuming rmp is a populated RefMultiPedigree...

for(rmp::pedigree_iterator ped = mp.pedigree_begin(); ped != mp.pedigree_end(); ++ped)
{
   for(rmp::member_const_iterator mem = ped->member_begin(); mem != ped->member_end(); ++mem)
   {
     // mem->do_something(...);
   }   
}      
```


# _sampling_ #

Sampling Library

Trait-based sampling mechanisms

## What is the sampling library? ##

The sampling library is designed to help you read in data from a
multipedigree and manage the trait data from it. Using the primary class, SAGE::SAMPLING::MemberDataSample, you can read in the multipedigree data and play around with their traits.

## How can I get started using the library? ##

Take a look at the documentation for SAGE::SAMPLING::MemberDataSample for more information.


# _util_ #

Miscellaneous S.A.G.E. Utilities

The util module contains small to mid-sized utility elements which are
general enough to be useful in more than one place, but small enough to not require their own module and unusual enough not to fit within any of the other existing modules.