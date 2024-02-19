# MenuService
MicroService for food ordering selling system that is used for managing the Menus of the Website. People can select from any Menu and buy it. Also people can publish their own menu and sell it.
The Service emits **Kafka event** when a Menu is created, that is consumed by **OrderService**.
