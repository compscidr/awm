<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        Schema::create('reporting_device', function (Blueprint $table) {
            $table->id('id');
            $table->string('uuid');
            $table->bigInteger('bt_mac_address');
            $table->bigInteger('wifi_mac_address');
            $table->integer('ipv4_address');
            $table->binary('ipv6_address');
            $table->timestamps();
            $table->float('longitude');
            $table->float('latitude');
            $table->string('OS');
            $table->float('battery_level');
            $table->tinyInteger('battery_charging');
            $table->tinyInteger('cellular_connected');
            $table->tinyInteger('wifi_connected');
            $table->float('wifi_throughput');
            $table->float('cellular_throughput');
            $table->integer('wifi_ping');
            $table->integer('cellular_ping');
            $table->string('cellular_operator');
            $table->integer('cellular_network_type');
        });
    }

    /**
     * Reverse the migrations.
     *
     * @return void
     */
    public function down()
    {
        Schema::dropIfExists('reporting_device');
    }
};