# SPL
Some of the projects from Systems programing class.
Assignment Definition
In our studio, we offer many workout plans (options). We have talented trainers who are experts in all
of our workout options (Yoga, Pilates, CrossFit, etc.), where each exercise can be Cardio Anaerobic
or Mixed. For example, Yoga is considered an anaerobic exercise, while spinning (cycling) is a mixed
exercise.
In this assignment, we made a C++ program that
simulates a fitness studio management system. The
program open the studio, assign customers to trainers,
make a workout plan that consists of one or more workout
options, calculate the trainer's salary, and other requests as
described below.
The program get a config file as an input, including all
required information about the fitness studio opening - the
number of trainers, how many customers each trainer can
handle, and details about the studio's possible workouts. A
detailed description of the input file will be defined in section
Each trainer is capable of performing all of the suggested workout types. There are four types of
customers in this studio. Each customer type has a unique workout strategy. A customer may request
a workout from a trainer more than once. In such cases, some customers may order an additional
and (maybe) different activity.
Each trainer in the studio has a limited amount of available spots in their workout session (provided in
the config file). Each trainer can handle multiple customers with more than one type of workout
simultaneously. In this studio, it's impossible to add new customers to an open workout session
(open trainer), but it's possible to move customers from one workout session to another between
different trainers under the trainer’s capacities.
The trainer's salary is the total price of all workout that was ordered for that trainer.

