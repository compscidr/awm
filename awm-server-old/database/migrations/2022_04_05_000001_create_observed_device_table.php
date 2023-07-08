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
        Schema::create('observed_device', function (Blueprint $table) {
            $table->id('id');
            $table->unsignedBigInteger('reporting_device_id');
            $table->bigInteger('mac_address');
            $table->tinyInteger('mac_type');
            $table->string('network_name');
            $table->integer('signal_strength');
            $table->integer('frequency');
            $table->integer('channel_width');
            $table->string('security');
            $table->foreign('reporting_device_id')->references('id')->on('reporting_device');
        });
    }

    /**
     * Reverse the migrations.
     *
     * @return void
     */
    public function down()
    {
        Schema::dropIfExists('observed_device');
    }
};