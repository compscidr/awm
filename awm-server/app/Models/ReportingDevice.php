<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class ReportingDevice extends Model
{
    use HasFactory;
    protected $fillable = ['uuid', 'bt_mac_address', 'wifi_mac_address', 'ipv4_address', 'ipv6_address', 'longitude', 'latitude', 'OS', 'battery_level', 'battery_charging', 'cellular_connected', 'wifi_connected', 'wifi_throughput', 'cellular_throughput', 'wifi_ping', 'cellular_ping', 'cellular_operator', 'cellular_network_type'];
}
